package com.lemonlightmc.zenith.events;

import java.util.function.Predicate;

import org.bukkit.event.Cancellable;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Common predicates for filtering Bukkit events in event subscriptions.
 */
@SuppressWarnings("unchecked")
public final class EventFilters {

  private static final Predicate<? extends Cancellable> IGNORE_CANCELLED = e -> !e.isCancelled();
  private static final Predicate<? extends PlayerLoginEvent> IGNORE_DISALLOWED_LOGIN = e -> e
      .getResult() == PlayerLoginEvent.Result.ALLOWED;
  private static final Predicate<? extends AsyncPlayerPreLoginEvent> IGNORE_DISALLOWED_PRE_LOGIN = e -> e
      .getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED;
  private static final Predicate<? extends PlayerMoveEvent> IGNORE_SAME_BLOCK = e -> e.getFrom().getBlockX() != e
      .getTo().getBlockX() ||
      e.getFrom().getBlockZ() != e.getTo().getBlockZ() ||
      e.getFrom().getBlockY() != e.getTo().getBlockY() ||
      !e.getFrom().getWorld().equals(e.getTo().getWorld());

  private static final Predicate<? extends PlayerMoveEvent> IGNORE_SAME_BLOCK_AND_Y = e -> e.getFrom().getBlockX() != e
      .getTo().getBlockX() ||
      e.getFrom().getBlockZ() != e.getTo().getBlockZ() ||
      !e.getFrom().getWorld().equals(e.getTo().getWorld());

  private static final Predicate<? extends PlayerMoveEvent> IGNORE_SAME_CHUNK = e -> (e.getFrom()
      .getBlockX() >> 4) != (e.getTo().getBlockX() >> 4) ||
      (e.getFrom().getBlockZ() >> 4) != (e.getTo().getBlockZ() >> 4) ||
      !e.getFrom().getWorld().equals(e.getTo().getWorld());

  /**
   * Returns a predicate that accepts only non-cancelled events.
   *
   * @param <T> the cancellable event type
   * @return a predicate matching non-cancelled events
   */
  public static <T extends Cancellable> Predicate<T> ignoreCancelled() {
    return (Predicate<T>) IGNORE_CANCELLED;
  }

  /**
   * Returns a predicate that accepts only cancelled events.
   *
   * @param <T> the cancellable event type
   * @return a predicate matching cancelled events
   */
  public static <T extends Cancellable> Predicate<T> ignoreNotCancelled() {
    return Cancellable::isCancelled;
  }

  /**
   * Returns a predicate that accepts only allowed player login events.
   *
   * @param <T> the player login event type
   * @return a predicate matching allowed login events
   */
  public static <T extends PlayerLoginEvent> Predicate<T> ignoreDisallowedLogin() {
    return (Predicate<T>) IGNORE_DISALLOWED_LOGIN;
  }

  /**
   * Returns a predicate that accepts only allowed async pre-login events.
   *
   * @param <T> the async pre-login event type
   * @return a predicate matching allowed pre-login events
   */
  public static <T extends AsyncPlayerPreLoginEvent> Predicate<T> ignoreDisallowedPreLogin() {
    return (Predicate<T>) IGNORE_DISALLOWED_PRE_LOGIN;
  }

  /**
   * Returns a predicate that accepts move events only when the block position changes.
   *
   * @param <T> the player move event type
   * @return a predicate matching moves to a different block
   */
  public static <T extends PlayerMoveEvent> Predicate<T> ignoreSameBlock() {
    return (Predicate<T>) IGNORE_SAME_BLOCK;
  }

  /**
   * Returns a predicate that accepts move events when block X/Z or world changes.
   *
   * @param <T> the player move event type
   * @return a predicate matching moves to a different block column or world
   */
  public static <T extends PlayerMoveEvent> Predicate<T> ignoreSameBlockAndY() {
    return (Predicate<T>) IGNORE_SAME_BLOCK_AND_Y;
  }

  /**
   * Returns a predicate that accepts move events only when the chunk changes.
   *
   * @param <T> the player move event type
   * @return a predicate matching moves to a different chunk
   */
  public static <T extends PlayerMoveEvent> Predicate<T> ignoreSameChunk() {
    return (Predicate<T>) IGNORE_SAME_CHUNK;
  }

  /**
   * Returns a predicate that accepts player events only when the player has the given permission.
   *
   * @param permission the required permission
   * @param <T> the player event type
   * @return a predicate matching events whose player has the permission
   */
  public static <T extends PlayerEvent> Predicate<T> playerHasPermission(final String permission) {
    return e -> e.getPlayer().hasPermission(permission);
  }
}
