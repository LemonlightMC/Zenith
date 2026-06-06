package com.lemonlightmc.zenith.utils;

import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.math.NumberConversions;
import com.lemonlightmc.zenith.messages.Logger;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.Result;
import net.luckperms.api.node.Node;
import net.luckperms.api.platform.PlayerAdapter;
import net.luckperms.api.util.Tristate;

public class PermissionUtils {
  private static PermissionHandler handler = null;
  private static PermissionOption currOption = null;

  private static final Set<PermissionOption> options = Set.of(
      new PermissionOption("LuckPerms", () -> PermissionHandlerLuckPerms.canEnable(), PermissionHandlerLuckPerms::new),
      new PermissionOption("Vault", () -> PermissionHandlerVault.canEnable(), PermissionHandlerVault::new),
      new PermissionOption("Default", () -> true, PermissionHandlerWildcard::new));

  public static int getPermissionValue(final CommandSender player, final String permission) {
    if (player == null || permission == null) {
      return -1;
    }
    if (permission == "*") {
      return Integer.MAX_VALUE;
    }
    try {
      return player.getEffectivePermissions().stream()
          .filter(perm -> perm.getValue() && perm.getPermission().startsWith(permission))
          .map(perm -> {
            final String perm2 = perm.getPermission().replace(permission, "");
            return perm2 == null ? 0 : perm2 == "*" ? Integer.MAX_VALUE : NumberConversions.parseInt(perm2);
          })
          .max(Integer::compareTo)
          .orElse(-1);
    } catch (final Exception e) {
      Logger.warn("&cInvalid permission value for &4" + player.getName() + "&c: &4" + permission);
      e.printStackTrace();
    }
    return -1;
  }

  public static Permission createPermission(String perm) {
    if (perm == null || perm.length() == 0) {
      return null;
    }
    perm = perm.toLowerCase(Locale.ENGLISH);
    final PermissionDefault defaultPerm = getDefault(perm);
    final Permission permission = new Permission(perm, defaultPerm);
    ZenithProvider.getInstance().getPluginManager().addPermission(permission);
    return permission;
  }

  public static Permission getPermission(String perm) {
    if (perm == null || perm.length() == 0) {
      return null;
    }
    perm = perm.toLowerCase(Locale.ENGLISH);
    return ZenithProvider.getInstance().getPluginManager().getPermission(perm);
  }

  public static Permission getOrCreatePermission(String perm) {
    if (perm == null || perm.length() == 0) {
      return null;
    }
    perm = perm.toLowerCase(Locale.ENGLISH);
    final Permission permission = ZenithProvider.getInstance().getPluginManager().getPermission(perm);
    return permission == null ? createPermission(perm) : permission;
  }

  public static boolean hasPermission(final CommandSender sender, String perm) {
    if (sender == null || perm == null || perm.length() == 0) {
      return false;
    }
    perm = perm.toLowerCase(Locale.ENGLISH);
    return handler == null ? sender.hasPermission(perm) : handler.hasPermission(sender, perm);
  }

  public static boolean hasPermission(final CommandSender sender, final String[] permissionNodePath) {
    return hasPermission(sender, String.join(".", permissionNodePath));
  }

  public static boolean hasDefaultPermission(final CommandSender sender, final String defaultPerm) {
    return hasDefaultPermission(sender, getDefault(getPermission(defaultPerm)));
  }

  public static boolean hasDefaultPermission(final CommandSender sender, final Permission defaultPerm) {
    return hasDefaultPermission(sender, getDefault(defaultPerm));
  }

  public static boolean hasDefaultPermission(final CommandSender sender, final PermissionDefault defaultPerm) {
    if (sender == null || defaultPerm == null) {
      return false;
    }
    return switch (defaultPerm) {
      case PermissionDefault.FALSE -> false;
      case PermissionDefault.TRUE -> true;
      case PermissionDefault.OP -> sender.isOp();
      case PermissionDefault.NOT_OP -> !sender.isOp();
      default -> false;
    };
  }

  public static PermissionDefault getDefault(final String perm) {
    return getDefault(getPermission(perm));
  }

  public static PermissionDefault getDefault(final Permission perm) {
    if (perm == null) {
      return PermissionDefault.FALSE;
    }
    boolean hasOP = false, hasNOTOP = false;
    switch (perm.getDefault()) {
      case TRUE:
        return PermissionDefault.TRUE;
      case OP:
        hasOP = true;
        break;
      case NOT_OP:
        hasNOTOP = true;
        break;
      default:
        break;
    }
    if (hasOP && hasNOTOP) {
      return PermissionDefault.TRUE;
    } else if (hasNOTOP) {
      return PermissionDefault.NOT_OP;
    } else if (hasOP) {
      return PermissionDefault.OP;
    }
    return PermissionDefault.FALSE;
  }

  public static PermissionHandler getHandler() {
    for (final PermissionOption opt : options) {
      if (!opt.isActive().get()) {
        continue;
      }
      if (opt != currOption) {
        currOption = opt;
        handler = opt.handler().get();
      }
      break;
    }
    return handler;
  }

  public static record PermissionOption(String name, Supplier<Boolean> isActive, Supplier<PermissionHandler> handler) {
  }

  public static interface PermissionHandler {

    public boolean hasPermission(final CommandSender sender, final String permission);
  }

  public static class PermissionHandlerWildcard implements PermissionHandler {
    @Override
    public boolean hasPermission(final CommandSender sender, final String permission) {
      return sender.hasPermission(getPermission(permission)) || permCheckWildcard(sender, permission);
    }

    protected boolean permCheckWildcard(final CommandSender sender, final String perm) {
      return permCheckWildcard(sender, new StringBuilder(perm.length()), perm.split("\\."), 0);
    }

    private static boolean permCheckWildcard(final CommandSender sender, final StringBuilder root, final String[] args,
        final int argIndex) {
      // Check the permission
      final String rootText = root.toString();
      if (!rootText.isEmpty() && PermissionUtils.getDefault(rootText) == PermissionDefault.TRUE) {
        return true;
      }
      // End of the sequence?
      if (argIndex >= args.length) {
        return false;
      }
      int rootLength = root.length();
      if (rootLength != 0) {
        root.append('.');
        rootLength++;
      }
      final int newArgIndex = argIndex + 1;
      // Check permission with original name
      root.append(args[argIndex].toLowerCase(Locale.ENGLISH));
      if (permCheckWildcard(sender, root, args, newArgIndex)) {
        return true;
      }

      // Try with *-signs
      root.setLength(rootLength);
      root.append('*');
      return permCheckWildcard(sender, root, args, newArgIndex);
    }
  }

  private static class PermissionHandlerVault extends PermissionHandlerWildcard {
    private static final String PERMISSION_TEST_NODE_ROOT = "moreutils.permission.testnode";
    private static final String PERMISSION_TEST_NODE = PERMISSION_TEST_NODE_ROOT + ".test";
    private static final String PERMISSION_TEST_NODE_ALL = PERMISSION_TEST_NODE_ROOT + ".*";
    private final net.milkbowl.vault.permission.Permission vault;
    private boolean hasWildcardSupport = false;

    public PermissionHandlerVault() {
      final RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> rsp = ZenithProvider
          .getInstance()
          .getServicesManager()
          .getRegistration(net.milkbowl.vault.permission.Permission.class);
      if (rsp != null) {
        this.vault = rsp.getProvider();
      } else {
        Logger.warn("Vault permission provider not found");
        vault = null;
        return;
      }
      hasWildcardSupport = detectWildcardSupport();
    }

    public static boolean canEnable() {
      return ZenithProvider.getInstance().getPluginManager().isPluginEnabled("Vault");
    }

    @Override
    public boolean hasPermission(final CommandSender sender, final String permission) {
      return _hasPermission(sender, permission) || (!hasWildcardSupport && permCheckWildcard(sender, permission));
    }

    @SuppressWarnings("deprecation")
    private boolean _hasPermission(final CommandSender sender, final String permission) {
      if (vault.hasSuperPermsCompat()) {
        return super.hasPermission(sender, permission);
      }

      // Initialize the permission (and it's default) prior to check
      final org.bukkit.permissions.Permission perm = getOrCreatePermission(permission);

      // Use a call here
      // First, make sure to handle our permission defaults
      if (perm.getDefault().getValue(sender.isOp())) {
        return true;
      }

      // Handle the remainder using Vault
      if (sender instanceof Player) {
        final Player p = (Player) sender;
        return vault.has(p.getWorld(), p.getName(), permission);
      } else {
        return vault.has(sender, permission);
      }
    }

    @SuppressWarnings("deprecation")
    protected boolean detectWildcardSupport() {
      // Bugfix for UPerms: hangs on retrieving the non-existent player profile name
      if (vault.getClass().getName().equals("me.TechsCode.UltraPermissions.hooks.pluginHooks.VaultPermissionHook")) {
        return true; // Appears to support it
      }
      // LuckPerms supports wildcards (not really used, we dont use this fallback)
      if (vault.getClass().getName().equals("me.lucko.luckperms.bukkit.vault.LuckPermsVaultPermission")) {
        return true;
      }

      // Perform a little experiment with a random player name
      // Give the player a test node, and see if the *-permission is handled right
      // Also pass in an invalid value to check that there are no inconsistencies
      final org.bukkit.permissions.Permission perm = PermissionUtils.getOrCreatePermission(PERMISSION_TEST_NODE);
      perm.setDefault(PermissionDefault.FALSE);

      // Find a player that doesnt have the permission (this is kinda pointless)
      final int maxTries = 10000;
      final String world = null;
      final String testPlayerNameBase = "TestPlayer";
      final StringBuilder testPlayerNameBldr = new StringBuilder(testPlayerNameBase);
      String testPlayerName = testPlayerNameBase;
      int i;
      for (i = 0; i < maxTries; i++) {
        testPlayerNameBldr.setLength(testPlayerNameBase.length());
        testPlayerNameBldr.append(i);
        testPlayerName = testPlayerNameBldr.toString();
        try {
          if (!vault.playerHas(world, testPlayerName, PERMISSION_TEST_NODE)) {
            break;
          }
        } catch (final Throwable t) {
          // Failure
          i = maxTries;
        }
      }

      // Check for permission failure (perhaps there is a consistent permission thing)
      boolean hasSupport = false;
      if (i < (maxTries - 1)) {
        try {
          // Grant permission with the *-node
          vault.playerAdd(world, testPlayerName, PERMISSION_TEST_NODE_ALL);

          // Adding was successful, ALWAYS do the removal phase
          // See if it had the desired effect
          try {
            hasSupport = vault.playerHas(world, testPlayerName, PERMISSION_TEST_NODE);
          } catch (final Throwable t) {
          }
          // Undo permission change
          try {
            vault.playerRemove(world, testPlayerName, PERMISSION_TEST_NODE_ALL);
          } catch (final Throwable t) {
          }
        } catch (final Throwable t) {
        }
      }

      ZenithProvider.getInstance().getPluginManager().removePermission(perm);
      return hasSupport;
    }
  }

  private static class PermissionHandlerLuckPerms implements PermissionHandler {
    private PlayerAdapter<Player> playerAdapter;
    private LuckPerms luckPerms;

    public PermissionHandlerLuckPerms() {
      try {
        final RegisteredServiceProvider<LuckPerms> rsp = ZenithProvider.getInstance()
            .getServicesManager()
            .getRegistration(LuckPerms.class);
        if (rsp != null && rsp.getProvider() != null) {
          this.luckPerms = rsp.getProvider();
        } else {
          this.luckPerms = LuckPermsProvider.get();
          if (luckPerms == null) {
            Logger.warn("LuckPerms permission provider not found");
            return;
          }
        }
        this.playerAdapter = luckPerms.getPlayerAdapter(Player.class);
      } catch (final Exception e) {
        Logger.warn("LuckPerms permission provider not found");
        this.playerAdapter = null;
      }
    }

    public static boolean canEnable() {
      return ZenithProvider.getInstance().getPluginManager().isPluginEnabled("LuckPerms");
    }

    @Override
    public boolean hasPermission(final CommandSender sender, final String permissionNode) {
      if (sender instanceof Player) {
        final Result<Tristate, Node> result = playerAdapter.getPermissionData((Player) sender)
            .queryPermission(permissionNode);
        if (result.node() != null) {
          return result.result().asBoolean();
        }
      } else {
        final Permission permission = ZenithProvider.getInstance().getPluginManager().getPermission(permissionNode);
        if (permission != null) {
          return sender.hasPermission(permission);
        }
      }

      return PermissionUtils.hasDefaultPermission(sender, permissionNode);
    }
  }
}
