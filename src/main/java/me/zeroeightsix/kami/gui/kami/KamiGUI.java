package me.zeroeightsix.kami.gui.kami;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.zeroeightsix.kami.KamiMod;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.gui.kami.component.ActiveModules;
import me.zeroeightsix.kami.gui.kami.component.Radar;
import me.zeroeightsix.kami.gui.kami.component.SettingsPanel;
import me.zeroeightsix.kami.gui.kami.theme.kami.KamiTheme;
import me.zeroeightsix.kami.gui.rgui.GUI;
import me.zeroeightsix.kami.gui.rgui.component.container.use.Frame;
import me.zeroeightsix.kami.gui.rgui.component.container.use.Scrollpane;
import me.zeroeightsix.kami.gui.rgui.component.listen.MouseListener;
import me.zeroeightsix.kami.gui.rgui.component.listen.TickListener;
import me.zeroeightsix.kami.gui.rgui.component.use.CheckButton;
import me.zeroeightsix.kami.gui.rgui.component.use.Label;
import me.zeroeightsix.kami.gui.rgui.render.theme.Theme;
import me.zeroeightsix.kami.gui.rgui.util.ContainerHelper;
import me.zeroeightsix.kami.gui.rgui.util.Docking;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.module.modules.gui.*;
import me.zeroeightsix.kami.module.modules.gui.Info.ColorEnum;
import me.zeroeightsix.kami.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 086 on 25/06/2017.
 */
public class KamiGUI extends GUI {

    public static final RootFontRenderer fontRenderer = new RootFontRenderer(1);
    public Theme theme;

    public static ColourHolder primaryColour = new ColourHolder(29, 29, 29);

    public KamiGUI() {
        super(new KamiTheme());
        theme = getTheme();
    }

    @Override
    public void drawGUI() {
        super.drawGUI();
    }

    @Override
    public void initializeGUI() {
        HashMap<Module.Category, Pair<Scrollpane, SettingsPanel>> categoryScrollpaneHashMap = new HashMap<>();
        for (Module module : ModuleManager.getModules()) {
            if (module.getCategory().isHidden()) continue;
            Module.Category moduleCategory = module.getCategory();
            if (!categoryScrollpaneHashMap.containsKey(moduleCategory)) {
                Stretcherlayout stretcherlayout = new Stretcherlayout(1);
                stretcherlayout.setComponentOffsetWidth(0);
                Scrollpane scrollpane = new Scrollpane(getTheme(), stretcherlayout, 300, 260);
                scrollpane.setMaximumHeight(180);
                categoryScrollpaneHashMap.put(moduleCategory, new Pair<>(scrollpane, new SettingsPanel(getTheme(), null)));
            }

            Pair<Scrollpane, SettingsPanel> pair = categoryScrollpaneHashMap.get(moduleCategory);
            Scrollpane scrollpane = pair.getKey();
            CheckButton checkButton = new CheckButton(module.getName());
            checkButton.setToggled(module.isEnabled());

            checkButton.addTickListener(() -> { // dear god
                checkButton.setToggled(module.isEnabled());
                checkButton.setName(module.getName());
            });

            checkButton.addMouseListener(new MouseListener() {
                @Override
                public void onMouseDown(MouseButtonEvent event) {
                    if (event.getButton() == 1) { // Right click
                        pair.getValue().setModule(module);
                        pair.getValue().setX(event.getX() + checkButton.getX());
                        pair.getValue().setY(event.getY() + checkButton.getY());
                    }
                }

                @Override
                public void onMouseRelease(MouseButtonEvent event) {

                }

                @Override
                public void onMouseDrag(MouseButtonEvent event) {

                }

                @Override
                public void onMouseMove(MouseMoveEvent event) {

                }

                @Override
                public void onScroll(MouseScrollEvent event) {

                }
            });
            checkButton.addPoof(new CheckButton.CheckButtonPoof<CheckButton, CheckButton.CheckButtonPoof.CheckButtonPoofInfo>() {
                @Override
                public void execute(CheckButton component, CheckButtonPoofInfo info) {
                    if (info.getAction().equals(CheckButton.CheckButtonPoof.CheckButtonPoofInfo.CheckButtonPoofInfoAction.TOGGLE)) {
                        module.setEnabled(checkButton.isToggled());
                    }
                }
            });
            scrollpane.addChild(checkButton);
        }

        int x = 10;
        int y = 10;
        int nexty = y;
        for (Map.Entry<Module.Category, Pair<Scrollpane, SettingsPanel>> entry : categoryScrollpaneHashMap.entrySet()) {
            Stretcherlayout stretcherlayout = new Stretcherlayout(1);
            stretcherlayout.COMPONENT_OFFSET_Y = 1;
            Frame frame = new Frame(getTheme(), stretcherlayout, entry.getKey().getName());
            Scrollpane scrollpane = entry.getValue().getKey();
            frame.addChild(scrollpane);
            frame.addChild(entry.getValue().getValue());
            scrollpane.setOriginOffsetY(0);
            scrollpane.setOriginOffsetX(0);
            frame.setCloseable(false);

            frame.setX(x);
            frame.setY(y);

            addChild(frame);

            nexty = Math.max(y + frame.getHeight() + 10, nexty);
            x += frame.getWidth() + 10;
            if (x > Wrapper.getMinecraft().displayWidth / 1.2f) {
                y = nexty;
                nexty = y;
            }
        }

        this.addMouseListener(new MouseListener() {
            private boolean isBetween(int min, int val, int max) {
                return !(val > max || val < min);
            }

            @Override
            public void onMouseDown(MouseButtonEvent event) {
                List<SettingsPanel> panels = ContainerHelper.getAllChildren(SettingsPanel.class, KamiGUI.this);
                for (SettingsPanel settingsPanel : panels) {
                    if (!settingsPanel.isVisible()) continue;
                    int[] real = GUI.calculateRealPosition(settingsPanel);
                    int pX = event.getX() - real[0];
                    int pY = event.getY() - real[1];
                    if (!isBetween(0, pX, settingsPanel.getWidth()) || !isBetween(0, pY, settingsPanel.getHeight()))
                        settingsPanel.setVisible(false);
                }
            }

            @Override
            public void onMouseRelease(MouseButtonEvent event) {

            }

            @Override
            public void onMouseDrag(MouseButtonEvent event) {

            }

            @Override
            public void onMouseMove(MouseMoveEvent event) {

            }

            @Override
            public void onScroll(MouseScrollEvent event) {

            }
        });
        

        ArrayList<Frame> frames = new ArrayList<>();

        Frame frame = new Frame(getTheme(), new Stretcherlayout(1), "Active modules");
        frame.setCloseable(false);
        frame.addChild(new ActiveModules());
        frame.setPinneable(true);
        frames.add(frame);

        frame = new Frame(getTheme(), new Stretcherlayout(1), "Info");
        frame.setCloseable(false);
        frame.setPinneable(true);
        Label information = new Label("");
        information.setShadow(true);
        information.addTickListener(() -> {
            boolean InfoWatermark = (((Info)ModuleManager.getModuleByName("Info")).watermark.getValue());
            boolean InfoTps = (((Info)ModuleManager.getModuleByName("Info")).tps.getValue());
            boolean InfoFps = (((Info)ModuleManager.getModuleByName("Info")).fps.getValue());
            boolean InfoName = (((Info)ModuleManager.getModuleByName("Info")).welcomer.getValue());
            boolean InfoPing = (((Info)ModuleManager.getModuleByName("Info")).ping.getValue());
           // boolean InfoPing = (((InfoOverlay)ModuleManager.getModuleByName("InfoOverlay")).globalInfoPin.getValue());
        	
                information.setText("");
                //white
             if(((Info)ModuleManager.getModuleByName("Info")).colorEnum.getValue().equals(ColorEnum.WHITE)) {
                if(InfoWatermark) {
                  information.addLine("Apollyon " + KamiMod.MODVER);
                }
                if(InfoName) {
                  //information.addLine("Hello " + Wrapper.getPlayer().getName() + " ^_^");
                     information.addLine("Merry Christmas " + Wrapper.getPlayer().getName() + " !");
                }
                if(InfoTps) {
                  information.addLine( Math.round(LagCompensator.INSTANCE.getTickRate()) + " TPS");
                }
                if(InfoFps) {
                  information.addLine(Wrapper.getMinecraft().debugFPS + " FPS");
                }
                if(InfoPing){
                  information.addLine(CalcPing.globalInfoPingValue() + "ms");
                }
            }
             //gray
             if(((Info)ModuleManager.getModuleByName("Info")).colorEnum.getValue().equals(ColorEnum.GRAY)) {
                 if(InfoWatermark) {
                   information.addLine("\u00A77" + "Apollyon " + "\u00A78" + KamiMod.MODVER);
                 }
                 if(InfoName) {
                   information.addLine("\u00A77" + "Merry Christmas " + "\u00A78" + Wrapper.getPlayer().getName() + "\u00A77" + " !");
                 }
                 if(InfoTps) {
                   information.addLine("\u00A77" + Math.round(LagCompensator.INSTANCE.getTickRate()) + "\u00A78" + " TPS");
                 }
                 if(InfoFps) {
                   information.addLine("\u00A77" + Wrapper.getMinecraft().debugFPS + "\u00A78" + " FPS");
                 }
                 if(InfoPing){
                     information.addLine("\u00A77" + CalcPing.globalInfoPingValue() + "\u00A78" + "ms");
                 }
             } 
             //blue
             if(((Info)ModuleManager.getModuleByName("Info")).colorEnum.getValue().equals(ColorEnum.BLUE)) {
                 if(InfoWatermark) {
                   information.addLine("\u00A79" + "Merry Christmas " + "\u00A71" + KamiMod.MODVER);
                 }
                 if(InfoName) {
                   information.addLine("\u00A79" + "Merry Christmas " + "\u00A71" + Wrapper.getPlayer().getName() + "\u00A79" + " !");
                 }
                 if(InfoTps) {
                   information.addLine("\u00A79" + Math.round(LagCompensator.INSTANCE.getTickRate()) + "\u00A71" + " TPS");
                 }
                 if(InfoFps) {
                   information.addLine("\u00A79" + Wrapper.getMinecraft().debugFPS + "\u00A71" + " FPS");
                 }
                 if(InfoPing){
                     information.addLine("\u00A79" + CalcPing.globalInfoPingValue() + "\u00A71"  + "ms");
                 }
             }
             //red
             if(((Info)ModuleManager.getModuleByName("Info")).colorEnum.getValue().equals(ColorEnum.RED)) {
                 if(InfoWatermark) {
                   information.addLine("\u00A7c" + "Apollyon " + "\u00A74" + KamiMod.MODVER);
                 }
                 if(InfoName) {
                   information.addLine("\u00A7c" + "Merry Christmas " + "\u00A74" + Wrapper.getPlayer().getName() + "\u00A7c" + " !");
                 }
                 if(InfoTps) {
                   information.addLine("\u00A7c" + Math.round(LagCompensator.INSTANCE.getTickRate()) + "\u00A74" + " TPS");
                 }
                 if(InfoFps) {
                   information.addLine("\u00A7c" + Wrapper.getMinecraft().debugFPS + "\u00A74" + " FPS");
                 }
                 if(InfoPing){
                     information.addLine("\u00A7c" + CalcPing.globalInfoPingValue() + "\u00A74"  + "ms");
                 }
             }
             //green
             if(((Info)ModuleManager.getModuleByName("Info")).colorEnum.getValue().equals(ColorEnum.GREEN)) {
                 if(InfoWatermark) {
                   information.addLine("\u00A7a" + "Apollyon " + "\u00A72" + KamiMod.MODVER);
                 }
                 if(InfoName) {
                   information.addLine("\u00A7a" + "Merry Christmas " + "\u00A72" + Wrapper.getPlayer().getName() + "\u00A7a" + " !");
                 }
                 if(InfoTps) {
                   information.addLine("\u00A7a" + Math.round(LagCompensator.INSTANCE.getTickRate()) + "\u00A72" + " TPS");
                 }
                 if(InfoFps) {
                   information.addLine("\u00A7a" + Wrapper.getMinecraft().debugFPS + "\u00A72" + " FPS");
                 }
                 if(InfoPing){
                     information.addLine("\u00A7a" + CalcPing.globalInfoPingValue() + "\u00A72" + "ms");
                 }
             }
             //yellow
             if(((Info)ModuleManager.getModuleByName("Info")).colorEnum.getValue().equals(ColorEnum.YELLOW)) {
                 if(InfoWatermark) {
                   information.addLine("\u00A7e" + "Apollyon " + "\u00A76" + KamiMod.MODVER);
                 }
                 if(InfoName) {
                   information.addLine("\u00A7e" + "Merry Christmas " + "\u00A76" + Wrapper.getPlayer().getName() + "\u00A7e" + " !");
                 }
                 if(InfoTps) {
                   information.addLine("\u00A7e" + Math.round(LagCompensator.INSTANCE.getTickRate()) + "\u00A76" + " TPS");
                 }
                 if(InfoFps) {
                   information.addLine("\u00A7e" + Wrapper.getMinecraft().debugFPS + "\u00A76" + " FPS");
                 }
                 if(InfoPing){
                     information.addLine("\u00A7e" + CalcPing.globalInfoPingValue() + "\u00A76" + "ms");
                 }
             }
             //purple
             if(((Info)ModuleManager.getModuleByName("Info")).colorEnum.getValue().equals(ColorEnum.PURPLE)) {
                 if(InfoWatermark) {
                   information.addLine("\u00A7d" + "Apollyon " + "\u00A75" + KamiMod.MODVER);
                 }
                 if(InfoName) {
                   information.addLine("\u00A7d" + "Merry Christmas " + "\u00A75" + Wrapper.getPlayer().getName() + "\u00A7d" + " !");
                 }
                 if(InfoTps) {
                   information.addLine("\u00A7d" + Math.round(LagCompensator.INSTANCE.getTickRate()) + "\u00A75" + " TPS");
                 }
                 if(InfoFps) {
                   information.addLine("\u00A7d" + Wrapper.getMinecraft().debugFPS + "\u00A75" + " FPS");
                 }
                 if(InfoPing){
                     information.addLine("\u00A7d" + CalcPing.globalInfoPingValue() + "\u00A75" + "ms");
                 }
             }

//            information.addLine("[&3" + Sprint.getSpeed() + "km/h&r]");

        });
        frame.addChild(information);
        information.setFontRenderer(fontRenderer);
        frames.add(frame);

        frame = new Frame(getTheme(), new Stretcherlayout(1), "Text Radar");
     //   Label information2 = new Label("");
        Label list = new Label("");
        DecimalFormat dfHealth = new DecimalFormat("#.#");
        dfHealth.setRoundingMode(RoundingMode.HALF_UP);
        StringBuilder healthSB = new StringBuilder();
        list.addTickListener(() -> {
            if (!list.isVisible()) return;
            list.setText("");

            Minecraft mc = Wrapper.getMinecraft();
          //  int i = 0;
            if (mc.player == null) return;
            List<EntityPlayer> entityList = mc.world.playerEntities;

            Map<String, Integer> players = new HashMap<>();
            for (Entity e : entityList) {
                if (e.getName().equals(mc.player.getName())) continue;
                float hpRaw = ((EntityLivingBase) e).getHealth() + ((EntityLivingBase) e).getAbsorptionAmount();
                String hp = dfHealth.format(hpRaw);
                healthSB.append(Command.SECTIONSIGN());
                if (hpRaw >= 20) {
                    healthSB.append("a");
                } else if (hpRaw >= 10) {
                    healthSB.append("e");
                } else if (hpRaw >= 5) {
                    healthSB.append("6");
                } else {
                    healthSB.append("c");
                }
                healthSB.append(hp);
                players.put(healthSB.toString() + " " + ChatFormatting.GRAY + e.getName(), (int) mc.player.getDistance(e));
                healthSB.setLength(0);
            }

            if (players.isEmpty()) {
                list.setText("");
                return;
            }

            players = sortByValue(players);

            for (Map.Entry<String, Integer> player : players.entrySet()) {
                list.addLine(Command.SECTIONSIGN() + "7" + player.getKey() + " " + Command.SECTIONSIGN() + "8" + player.getValue());
            }
        });
        frame.setCloseable(false);
        frame.setPinneable(true);
        frame.setMinimumWidth(75);
        list.setShadow(true);
        frame.addChild(list);
        list.setFontRenderer(fontRenderer);
        frames.add(frame);

        frame = new Frame(getTheme(), new Stretcherlayout(1), "Entities");
        Label entityLabel = new Label("");
        frame.setCloseable(false);
        entityLabel.addTickListener(new TickListener() {
            Minecraft mc = Wrapper.getMinecraft();

            @Override
            public void onTick() {
                if (mc.player == null || !entityLabel.isVisible()) return;

                final List<Entity> entityList = new ArrayList<>(mc.world.loadedEntityList);
                if (entityList.size() <= 1) {
                    entityLabel.setText("");
                    return;
                }
                final Map<String, Integer> entityCounts = entityList.stream()
                        .filter(Objects::nonNull)
                        .filter(e -> !(e instanceof EntityPlayer))
                        .collect(Collectors.groupingBy(KamiGUI::getEntityName,
                                Collectors.reducing(0, ent -> {
                                    if (ent instanceof EntityItem)
                                        return ((EntityItem)ent).getItem().getCount();
                                    return 1;
                                }, Integer::sum)
                        ));

                entityLabel.setText("");
                entityCounts.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .map(entry -> TextFormatting.GRAY + entry.getKey() + " " + TextFormatting.DARK_GRAY + "x" + entry.getValue())
                        .forEach(entityLabel::addLine);

                //entityLabel.getParent().setHeight(entityLabel.getLines().length * (entityLabel.getTheme().getFontRenderer().getFontHeight()+1) + 3);
            }
        });
        frame.addChild(entityLabel);
        frame.setPinneable(true);
        entityLabel.setShadow(true);
        entityLabel.setFontRenderer(fontRenderer);
        frames.add(frame);
        
        frame = new Frame(getTheme(), new Stretcherlayout(1), "Coordinates");
        frame.setCloseable(false);
        frame.setPinneable(true);
        Label coordsLabel = new Label("");
        coordsLabel.addTickListener(new TickListener() {
            Minecraft mc = Minecraft.getMinecraft();

            @Override
            public void onTick() {
                boolean inHell = (mc.world.getBiome(mc.player.getPosition()).getBiomeName().equals("Hell"));

                int posX = (int) mc.player.posX;
                int posY = (int) mc.player.posY;
                int posZ = (int) mc.player.posZ;

                float f = !inHell ? 0.125f : 8;
                int hposX = (int) (mc.player.posX * f);
                int hposZ = (int) (mc.player.posZ * f);

                coordsLabel.setText(String.format(" %sf%,d%s7, %sf%,d%s7, %sf%,d %s7(%sf%,d%s7, %sf%,d%s7, %sf%,d%s7)",
                        Command.SECTIONSIGN(),
                        posX,
                        Command.SECTIONSIGN(),
                        Command.SECTIONSIGN(),
                        posY,
                        Command.SECTIONSIGN(),
                        Command.SECTIONSIGN(),
                        posZ,
                        Command.SECTIONSIGN(),
                        Command.SECTIONSIGN(),
                        hposX,
                        Command.SECTIONSIGN(),
                        Command.SECTIONSIGN(),
                        posY,
                        Command.SECTIONSIGN(),
                        Command.SECTIONSIGN(),
                        hposZ,
                        Command.SECTIONSIGN()
                ));
            }
        });
        frame.addChild(coordsLabel);
        coordsLabel.setFontRenderer(fontRenderer);
        coordsLabel.setShadow(true);
        frame.setHeight(20);
        frames.add(frame);

        frame = new Frame(getTheme(), new Stretcherlayout(1), "Radar");
        frame.setCloseable(false);
        frame.setMinimizeable(true);
        frame.setPinneable(true);
        frame.addChild(new Radar());
        frame.setWidth(100);
        frame.setHeight(100);
        frames.add(frame);

        for (Frame frame1 : frames) {
            frame1.setX(x);
            frame1.setY(y);

            nexty = Math.max(y + frame1.getHeight() + 10, nexty);
            x += frame1.getWidth() + 10;
            if (x * DisplayGuiScreen.getScale() > Wrapper.getMinecraft().displayWidth / 1.2f) {
                y = nexty;
                nexty = y;
                x = 10;
            }

            addChild(frame1);
        }
    }


    private static String getEntityName(@Nonnull Entity entity) {
        if (entity instanceof EntityItem) {
            return TextFormatting.DARK_AQUA + ((EntityItem) entity).getItem().getItem().getItemStackDisplayName(((EntityItem) entity).getItem());
        }
        if (entity instanceof EntityWitherSkull) {
            return TextFormatting.DARK_GRAY + "Wither skull";
        }
        if (entity instanceof EntityEnderCrystal) {
            return TextFormatting.LIGHT_PURPLE + "End crystal";
        }
        if (entity instanceof EntityEnderPearl) {
            return "Thrown ender pearl";
        }
        if (entity instanceof EntityMinecart) {
            return "Minecart";
        }
        if (entity instanceof EntityItemFrame) {
            return "Item frame";
        }
        if (entity instanceof EntityEgg) {
            return "Thrown egg";
        }
        if (entity instanceof EntitySnowball) {
            return "Thrown snowball";
        }

        return entity.getName();
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list =
                new LinkedList<>(map.entrySet());
        Collections.sort(list, Comparator.comparing(o -> (o.getValue())));

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
    

    @Override
    public void destroyGUI() {
        kill();
    }

    private static final int DOCK_OFFSET = 0;

    public static void dock(Frame component) {
        Docking docking = component.getDocking();
        if (docking.isTop())
            component.setY(DOCK_OFFSET);
        if (docking.isBottom())
            component.setY((Wrapper.getMinecraft().displayHeight / DisplayGuiScreen.getScale()) - component.getHeight() - DOCK_OFFSET);
        if (docking.isLeft())
            component.setX(DOCK_OFFSET);
        if (docking.isRight())
            component.setX((Wrapper.getMinecraft().displayWidth / DisplayGuiScreen.getScale()) - component.getWidth() - DOCK_OFFSET);
        if (docking.isCenterHorizontal())
            component.setX((Wrapper.getMinecraft().displayWidth / (DisplayGuiScreen.getScale() * 2) - component.getWidth() / 2));
        if (docking.isCenterVertical())
            component.setY(Wrapper.getMinecraft().displayHeight / (DisplayGuiScreen.getScale() * 2) - component.getHeight() / 2);
    }
}
