package com.lemonlightmc.zenith.integrations;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.additive.math.NumberConversions;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.context.Context;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;

public class LuckPerms {

  public static net.luckperms.api.LuckPerms get() {
    return LuckPermsProvider.get();
  }

  public static net.luckperms.api.LuckPerms getOrNull() {
    try {
      return LuckPermsProvider.get();
    } catch (final Exception e) {
      return null;
    }
  }

  public static net.luckperms.api.LuckPerms getOrThrow() {
    return LuckPermsProvider.get();
  }

  public static boolean isSupported() {
    try {
      return LuckPermsProvider.get() != null;
    } catch (final Exception e) {
      return false;
    }
  }

  public static boolean isEnabled() {
    return ZenithProvider.instance().getPluginManager().isPluginEnabled("LuckPerms");
  }

  // Permissions
  public static boolean hasPermission(final Player player, final String permission) {
    if (player == null) {
      return false;
    }
    if (permission == null || permission.isEmpty()) {
      return true;
    }
    try {
      final CachedPermissionData permData = LuckPermsProvider.get().getPlayerAdapter(Player.class)
          .getPermissionData(player);
      return permData != null && permData.checkPermission(permission).asBoolean();
    } catch (final Exception e) {
      return false;
    }
  }

  public static boolean hasPermission(final UUID uniqueId, final String permission) {
    return hasPermission(LuckPermsProvider.get().getUserManager().getUser(uniqueId), permission);
  }

  public static boolean hasPermission(final User user, final String permission) {
    if (user == null) {
      return false;
    }
    if (permission == null || permission.isEmpty()) {
      return true;
    }
    return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
  }

  public static boolean hasPermission(final CommandSender sender, final String permission) {
    if (sender == null) {
      return false;
    }
    if (sender instanceof final Player player) {
      return hasPermission(player, permission);
    } else {
      return sender.hasPermission(permission);
    }
  }

  public static int getPermissionIntValue(final UUID uuid, final String permission) {
    return getPermissionIntValue(getUser(uuid), permission);
  }

  public static int getPermissionIntValue(final User user, final String permission) {
    if (user == null) {
      return Integer.MIN_VALUE;
    }
    return queryPermissionIntValue(user.getCachedData().getPermissionData(), permission);
  }

  public static int getPermissionIntValue(final Player player, final String permission) {
    if (player == null) {
      return Integer.MIN_VALUE;
    }
    return queryPermissionIntValue(LuckPermsProvider.get().getPlayerAdapter(Player.class).getPermissionData(player),
        permission);
  }

  private static int queryPermissionIntValue(final CachedPermissionData data, final String permission) {
    if (data == null) {
      return Integer.MIN_VALUE;
    }
    if (permission == null || permission.isEmpty() || permission.equals("*")) {
      return Integer.MAX_VALUE;
    }

    for (final Map.Entry<String, Boolean> entry : data.getPermissionMap().entrySet()) {
      final String key = entry.getKey();
      if (key.equals("*") && entry.getValue()) {
        return Integer.MAX_VALUE;
      }
      if (!permission.startsWith(key)) {
        return Integer.MIN_VALUE;
      }
      if (key.endsWith("*")) {
        return Integer.MAX_VALUE;
      }
      final String suffix = key.substring(key.lastIndexOf(".") + 1);
      try {
        return NumberConversions.parseInt(suffix);
      } catch (final NumberFormatException e) {
        return Integer.MIN_VALUE;
      }
    }
    return Integer.MIN_VALUE;
  }

  // User
  public static User getUser(final UUID uuid) {
    if (uuid == null) {
      return null;
    }
    return LuckPermsProvider.get().getUserManager().getUser(uuid);
  }

  public static User getUser(final Player player) {
    if (player == null) {
      return null;
    }
    return LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
  }

  public static User getUser(final String name) {
    if (name == null || name.isEmpty()) {
      return null;
    }
    return LuckPermsProvider.get().getUserManager().getUser(name);
  }

  // Primary Group
  public static String getPrimaryGroup(final UUID uuid) {
    return getPrimaryGroup(getUser(uuid));
  }

  public static String getPrimaryGroup(final Player player) {
    return getPrimaryGroup(getUser(player));
  }

  public static String getPrimaryGroup(final User user) {
    return user == null ? null : user.getPrimaryGroup();
  }

  // UserNodes
  public static Collection<Node> getUserNodes(final UUID uuid) {
    return getUserNodes(getUser(uuid));
  }

  public static Collection<Node> getUserNodes(final Player player) {
    return getUserNodes(getUser(player.getUniqueId()));
  }

  public static Collection<Node> getUserNodes(final User user) {
    return user == null ? null : user.getNodes();
  }

  // MetaData
  public static CachedMetaData getUserMetadata(final UUID uuid) {
    return getUserMetadata(getUser(uuid));
  }

  public static CachedMetaData getUserMetadata(final Player player) {
    return getUserMetadata(getUser(player.getUniqueId()));
  }

  public static CachedMetaData getUserMetadata(final User user) {
    return user == null ? null : user.getCachedData().getMetaData();
  }

  public static String getUserMetadataValue(final UUID uuid, final String key) {
    return getUserMetadataValue(getUser(uuid), key);
  }

  public static String getUserMetadataValue(final Player player, final String key) {
    return getUserMetadataValue(getUser(player.getUniqueId()), key);
  }

  public static String getUserMetadataValue(final User user, final String key) {
    return user == null ? null : user.getCachedData().getMetaData().getMetaValue(key);
  }

  // Prefix & Suffix
  public static String getUserPrefix(final UUID uuid) {
    return getUserPrefix(getUser(uuid));
  }

  public static String getUserPrefix(final Player player) {
    return getUserPrefix(getUser(player.getUniqueId()));
  }

  public static String getUserPrefix(final User user) {
    return user == null ? null : user.getCachedData().getMetaData().getPrefix();
  }

  public static String getUserSuffix(final UUID uuid) {
    return getUserSuffix(getUser(uuid));
  }

  public static String getUserSuffix(final Player player) {
    return getUserSuffix(getUser(player.getUniqueId()));
  }

  public static String getUserSuffix(final User user) {
    return user == null ? null : user.getCachedData().getMetaData().getSuffix();
  }

  // groups
  public static Group getGroup(final String str) {
    if (str == null || str.isEmpty()) {
      return null;
    }
    return LuckPermsProvider.get().getGroupManager().getGroup(str);
  }

  public static CompletableFuture<Group> createGroup(final String name) {
    if (name == null || name.isEmpty()) {
      return CompletableFuture.completedFuture(null);
    }
    return LuckPermsProvider.get().getGroupManager().createAndLoadGroup(name);
  }

  public static void deleteGroup(final String name) {
    if (name == null || name.isEmpty()) {
      return;
    }
    deleteGroup(getGroup(name));
  }

  public static void deleteGroup(final Group group) {
    if (group == null) {
      return;
    }
    LuckPermsProvider.get().getGroupManager().deleteGroup(group);
  }

  public static void saveGroup(final String name) {
    if (name == null || name.isEmpty()) {
      return;
    }
    saveGroup(getGroup(name));
  }

  public static void saveGroup(final Group group) {
    if (group == null) {
      return;
    }
    LuckPermsProvider.get().getGroupManager().saveGroup(group);
  }

  public static CompletableFuture<Group> loadGroup(final String name) {
    if (name == null || name.isEmpty()) {
      return CompletableFuture.completedFuture(null);
    }
    return LuckPermsProvider.get().getGroupManager().loadGroup(name).thenApply(o -> o.orElse(null));
  }

  public static void loadGroup(final Group group) {
    if (group == null) {
      return;
    }
    loadGroup(group.getName());
  }

  public static Set<Group> getLoadedGroups() {
    return LuckPermsProvider.get().getGroupManager().getLoadedGroups();
  }

  public static boolean isLoaded(final String name) {
    return name == null || name.isEmpty() ? false : LuckPermsProvider.get().getGroupManager().isLoaded(name);
  }

  public static boolean isLoaded(final Group group) {
    return group == null ? false : isLoaded(group.getName());
  }

  public static boolean isCreated(final String name) {
    return name == null || name.isEmpty() ? false : getGroup(name) != null;
  }

  public static void modifyGroup(final String name, final Consumer<? super Group> action,
      final boolean onlyModifyIfExists) {
    if (name == null || name.isEmpty()) {
      return;
    }
    if (onlyModifyIfExists && getGroup(name) == null) {
      return;
    }
    LuckPermsProvider.get().getGroupManager().modifyGroup(name, action);
  }

  public static void modifyGroup(final Group group, final Consumer<? super Group> action) {
    if (group == null) {
      return;
    }
    LuckPermsProvider.get().getGroupManager().modifyGroup(group.getName(), action);
  }

  // Group Nodes
  public static Collection<Node> getGroupNodes(final Group group) {
    return group == null ? null : group.getNodes();
  }

  public static Collection<Node> getGroupNodes(final String str) {
    if (str == null || str.isEmpty()) {
      return null;
    }
    final Group group = LuckPermsProvider.get().getGroupManager().getGroup(str);
    return group == null ? null : group.getNodes();
  }

  // Group Nodes Value
  public static String getGroupNodeValue(final UUID uuid, final String startsWith) {
    try {
      final Group group = getGroup(getPrimaryGroup(uuid));
      if (group == null) {
        return null;
      }

      for (final Node node : group.getNodes()) {
        final String key = node.getKey();
        if (key.startsWith(startsWith)) {
          final String[] split = key.split("\\.");
          return split.length > 2 ? split[2] : split[1];
        }
      }
    } catch (final Exception var18) {
    }

    return null;
  }

  // Contexts
  public static ImmutableContextSet getUserContexts(final UUID uuid) {
    return getUserContexts(getUser(uuid));
  }

  public static ImmutableContextSet getUserContexts(final Player player) {
    return player == null ? null : LuckPermsProvider.get().getContextManager().getContext(player);
  }

  public static ImmutableContextSet getUserContexts(final User user) {
    return user == null ? null : LuckPermsProvider.get().getContextManager().getContext(user).orElse(null);
  }

  public static ImmutableContextSet getStaticContext() {
    return LuckPermsProvider.get().getContextManager().getStaticContext();
  }

  public static boolean hasContext(final User user) {
    if (user == null) {
      return false;
    }
    final ImmutableContextSet set = LuckPermsProvider.get().getContextManager().getContext(user).orElse(null);
    return set != null && !set.isEmpty();
  }

  public static boolean hasContext(final UUID uuid) {
    return hasContext(getUser(uuid));
  }

  public static boolean hasContext(final Player player) {
    return hasContext(getUser(player));
  }

  public static boolean hasContext(final User user, final String key) {
    if (user == null || key == null || key.isEmpty()) {
      return false;
    }
    final ImmutableContextSet set = LuckPermsProvider.get().getContextManager().getContext(user).orElse(null);
    return set != null && !set.isEmpty() && set.containsKey(key);
  }

  public static boolean hasContext(final UUID uuid, final String key) {
    return hasContext(getUser(uuid), key);
  }

  public static boolean hasContext(final Player player, final String key) {
    return hasContext(getUser(player), key);
  }

  public static boolean hasContext(final User user, final Context context) {
    if (user == null || context == null) {
      return false;
    }
    final ImmutableContextSet set = LuckPermsProvider.get().getContextManager().getContext(user).orElse(null);
    return set != null && !set.isEmpty() && set.contains(context);
  }

  public static boolean hasContext(final UUID uuid, final Context context) {
    return hasContext(getUser(uuid), context);
  }

  public static boolean hasContext(final Player player, final Context context) {
    return hasContext(getUser(player), context);
  }

  public static boolean hasStaticContext() {
    final ImmutableContextSet set = LuckPermsProvider.get().getContextManager().getStaticContext();
    return set != null && !set.isEmpty();
  }

  public static boolean hasStaticContext(final String key) {
    if (key == null || key.isEmpty()) {
      return false;
    }
    final ImmutableContextSet set = LuckPermsProvider.get().getContextManager().getStaticContext();
    return set != null && !set.isEmpty() && set.containsKey(key);
  }

  public static boolean hasStaticContext(final Context context) {
    if (context == null) {
      return false;
    }
    final ImmutableContextSet set = LuckPermsProvider.get().getContextManager().getStaticContext();
    return set != null && !set.isEmpty() && set.contains(context);
  }

  public static void registerContext(final ContextCalculator<?> calculator) {
    if (calculator == null) {
      return;
    }
    LuckPermsProvider.get().getContextManager().registerCalculator(calculator);
  }

  public static void registerContext(final Class<? extends ContextCalculator<?>> clazz) {
    if (clazz == null) {
      return;
    }
    try {
      registerContext(clazz.getDeclaredConstructor().newInstance());
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  public static void unregisterContext(final ContextCalculator<?> calculator) {
    if (calculator == null) {
      return;
    }
    LuckPermsProvider.get().getContextManager().unregisterCalculator(calculator);
  }

  public static abstract class LuckPermsContextCalculator<T> implements ContextCalculator<T> {
    static <T> ContextCalculator<T> forSingleContext(final String key, final Function<T, String> valueFunction) {
      if (key == null || key.isEmpty() || valueFunction == null) {
        throw new IllegalArgumentException("Key and value function must not be null or empty");
      }
      return (target, consumer) -> {
        final String value = valueFunction.apply(target);
        if (value != null) {
          consumer.accept(key, value);
        }
      };
    }

    public LuckPermsContextCalculator<T> register() {
      LuckPermsProvider.get().getContextManager().registerCalculator(this);
      return this;
    }

    @Override
    public abstract void calculate(T target, ContextConsumer consumer);

    @Override
    public ContextSet estimatePotentialContexts() {
      return ImmutableContextSet.empty();
    }
  }
}
