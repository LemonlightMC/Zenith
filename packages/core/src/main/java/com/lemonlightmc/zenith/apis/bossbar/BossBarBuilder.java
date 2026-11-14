package com.lemonlightmc.zenith.apis.bossbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.lemonlightmc.zenith.additive.interfaces.Builder;
import com.lemonlightmc.zenith.additive.interfaces.Cloneable;

import java.lang.reflect.Type;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.serialize.SerializationException;

import net.md_5.bungee.api.chat.BaseComponent;

public class BossBarBuilder implements Builder<BossBar>, Cloneable<BossBarBuilder> {

    public static final BossBarSerializer SERIALIZER = new BossBarSerializer();

    private NamespacedKey key;
    private String title;
    private BarColor color = BossBar.DEFAULT_COLOR;
    private BarStyle style = BossBar.DEFAULT_STYLE;
    private boolean visible = BossBar.DEFAULT_VISIBILITY;
    private double progress = BossBar.DEFAULT_PROGRESS;
    private Set<BarFlag> flags = new HashSet<>();
    private List<Player> players = new ArrayList<>();

    private long interval = -1;
    private long duration = -1;
    private Consumer<BossBar> consumer;
    private long delay = 0;

    public BossBarBuilder(final NamespacedKey key) {
        if (key == null) {
            throw new IllegalArgumentException("Bossbar Key cannot be null");
        }
        this.key = key;
    }

    public BossBarBuilder(final BossBarBuilder builder) {
        if (builder == null) {
            throw new IllegalArgumentException("Bossbar Builder cannot be null");
        }
        this.key = builder.key;
        this.title = builder.title;
        this.color = builder.color;
        this.style = builder.style;
        this.visible = builder.visible;
        this.progress = builder.progress;
        this.flags = builder.flags;
        this.players = builder.players;
        this.interval = builder.interval;
        this.consumer = builder.consumer;
    }

    public BossBarBuilder() {
    }

    public static BossBarBuilder create(final NamespacedKey key) {
        return new BossBarBuilder(key);
    }

    public static BossBarBuilder create() {
        return new BossBarBuilder();
    }

    public BossBarBuilder key(final NamespacedKey key) {
        if (key == null) {
            throw new IllegalArgumentException("Bossbar Key cannot be null");
        }
        this.key = key;
        return this;
    }

    public NamespacedKey key() {
        return key;
    }

    public BossBarBuilder title(final BaseComponent title) {
        this.title = title.toLegacyText();
        return this;
    }

    public BossBarBuilder title(final String title) {
        this.title = title;
        return this;
    }

    public String title() {
        return title;
    }

    public BossBarBuilder color(final BarColor color) {
        this.color = color;
        return this;
    }

    public BarColor color() {
        return color;
    }

    public BossBarBuilder style(final BarStyle style) {
        this.style = style;
        return this;
    }

    public BarStyle style() {
        return style;
    }

    public BossBarBuilder visible(final boolean visible) {
        this.visible = visible;
        return this;
    }

    public boolean visible() {
        return visible;
    }

    public BossBarBuilder progress(final double progress) {
        this.progress = progress;
        return this;
    }

    public double progress() {
        return progress;
    }

    public Set<BarFlag> flags() {
        return flags;
    }

    public BossBarBuilder flags(final BarFlag... flag) {
        this.flags.addAll(List.of(flag));
        return this;
    }

    public BossBarBuilder flags(final BarFlag flag) {
        this.flags.add(flag);
        return this;
    }

    public BossBarBuilder removeFlag(final BarFlag flag) {
        this.flags.remove(flag);
        return this;
    }

    public BossBarBuilder setFlags(final Set<BarFlag> flags) {
        this.flags = flags;
        return this;
    }

    public BossBarBuilder clearFlags() {
        this.flags.clear();
        return this;
    }

    public List<Player> players() {
        return players;
    }

    public BossBarBuilder players(final Player... players) {
        this.players.addAll(List.of(players));
        return this;
    }

    public BossBarBuilder players(final Player player) {
        this.players.add(player);
        return this;
    }

    public BossBarBuilder removePlayer(final Player player) {
        this.players.remove(player);
        return this;
    }

    public BossBarBuilder setPlayers(final List<Player> players) {
        this.players = players;
        return this;
    }

    public long duration() {
        return duration;
    }

    public BossBarBuilder duration(final long duration) {
        this.duration = Math.min(Math.max(duration, 0), Long.MAX_VALUE);
        return this;
    }

    public long delay() {
        return delay;
    }

    public BossBarBuilder delay(final long delay) {
        this.delay = Math.min(Math.max(delay, 0), Long.MAX_VALUE);
        return this;
    }

    public Consumer<BossBar> updateEvery() {
        return consumer;
    }

    public BossBarBuilder updateEvery(final long interval) {
        this.interval = Math.min(Math.max(interval, 0), Long.MAX_VALUE);
        if (this.interval > this.duration) {
            this.interval = this.duration;
        }
        return this;
    }

    public BossBarBuilder updateAction(final Consumer<BossBar> consumer) {
        this.consumer = consumer;
        return this;
    }

    public Consumer<BossBar> updateAction() {
        return consumer;
    }

    public BossBarBuilder clone() {
        return new BossBarBuilder(this);
    }

    public BossBar build() {
        if (key == null) {
            throw new IllegalArgumentException("Bossbar Key cannot be null");
        }
        final BossBar bar = new BossBar(key, title, color, style, flags);
        bar.setProgress(this.progress);
        bar.setVisible(visible);
        bar.addPlayers(players);
        bar.appendTimer(new BossBarTimer(bar, interval, duration, delay, consumer));
        return bar;
    }

    public static final class BossBarSerializer implements TypeSerializer<BossBar> {

        private static final String KEY = "key";
        private static final String TITLE = "title";
        private static final String COLOR = "color";
        private static final String STYLE = "style";
        private static final String PROGRESS = "progress";
        private static final String VISIBLE = "visible";
        private static final String FLAGS = "flags";

        private BossBarSerializer() {
        }

        @Override
        public BossBar deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
            if (node.virtual() || node.isNull()) {
                return null;
            }

            final String keyString = node.node(KEY).getString();
            if (keyString == null || keyString.isEmpty()) {
                throw new SerializationException(node, BossBar.class, "BossBar requires a non-empty 'key'");
            }
            final NamespacedKey key = NamespacedKey.fromString(keyString);
            if (key == null) {
                throw new SerializationException(node.node(KEY), NamespacedKey.class,
                        "Invalid bossbar key: " + keyString);
            }

            final String title = node.node(TITLE).getString("");
            final BarColor color = parseEnum(node.node(COLOR), BarColor.class, BossBar.DEFAULT_COLOR);
            final BarStyle style = parseEnum(node.node(STYLE), BarStyle.class, BossBar.DEFAULT_STYLE);

            final Set<BarFlag> flags = new java.util.HashSet<>();
            final ConfigurationNode flagsNode = node.node(FLAGS);
            if (!flagsNode.virtual() && !flagsNode.isNull()) {
                if (flagsNode.isList()) {
                    for (final ConfigurationNode flagNode : flagsNode.childrenList()) {
                        final BarFlag flag = parseEnum(flagNode, BarFlag.class, null);
                        if (flag != null) {
                            flags.add(flag);
                        }
                    }
                } else {
                    final BarFlag flag = parseEnum(flagsNode, BarFlag.class, null);
                    if (flag != null) {
                        flags.add(flag);
                    }
                }
            }

            final BossBar bar = new BossBar(key, title, color, style, flags);

            if (!node.node(PROGRESS).virtual()) {
                bar.setProgress(node.node(PROGRESS).getDouble(BossBar.DEFAULT_PROGRESS));
            }
            if (!node.node(VISIBLE).virtual()) {
                bar.setVisible(node.node(VISIBLE).getBoolean(BossBar.DEFAULT_VISIBILITY));
            }

            return bar;
        }

        @Override
        public void serialize(final Type type, final @Nullable BossBar obj, final ConfigurationNode node)
                throws SerializationException {
            if (obj == null) {
                node.set(null);
                return;
            }

            node.node(KEY).set(obj.getKey().toString());
            node.node(TITLE).set(obj.getTitle());
            node.node(COLOR).set(obj.getColor().name());
            node.node(STYLE).set(obj.getStyle().name());
            node.node(PROGRESS).set(obj.getProgress());
            node.node(VISIBLE).set(obj.getVisible());

            final Set<BarFlag> flags = obj.getFlags();
            if (flags == null || flags.isEmpty()) {
                node.node(FLAGS).setList(String.class, List.of());
            } else {
                final List<String> names = new ArrayList<>(flags.size());
                for (final BarFlag flag : flags) {
                    names.add(flag.name());
                }
                node.node(FLAGS).setList(String.class, names);
            }
        }

        private static <E extends Enum<E>> E parseEnum(final ConfigurationNode node, final Class<E> type, final E def) {
            final String value = node.getString();
            if (value == null || value.isEmpty()) {
                return def;
            }
            try {
                return Enum.valueOf(type, value.toUpperCase(java.util.Locale.ROOT));
            } catch (final IllegalArgumentException ex) {
                return def;
            }
        }
    }

}
