package com.lemonlightmc.zenith.messages;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Audience implements IAudience<CommandSender> {

  private final Set<CommandSender> viewers;
  private final Set<String> permissions = new HashSet<>();
  private final Set<Predicate<CommandSender>> requirements = new HashSet<>();
  private boolean isFiltered = false;

  public Audience() {
    this.viewers = Collections.newSetFromMap(new ConcurrentHashMap<>());
  }

  public Audience(final Collection<CommandSender> viewers) {
    this.viewers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    this.viewers.addAll(viewers);
  }

  public Audience(final CommandSender[] viewers) {
    this.viewers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    this.viewers.addAll(List.of(viewers));
  }

  public Audience(final CommandSender viewer) {
    this.viewers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    this.viewers.add(viewer);
  }

  public static Audience onlinePlayers() {
    Audience a = new Audience();
    a.addViewers(Bukkit.getOnlinePlayers().toArray(CommandSender[]::new));
    return a;
  }

  public static Audience player(Player p) {
    return new Audience(p);
  }

  public static Audience sender(CommandSender p) {
    return new Audience(p);
  }

  @Override
  public Set<CommandSender> viewers() {
    return viewers;
  }

  private void applyGlobalFilter() {
    Iterator<CommandSender> iter = viewers.iterator();
    while (iter.hasNext()) {
      CommandSender sender = iter.next();
      for (String perm : permissions) {
        if (!sender.hasPermission(perm)) {
          iter.remove();
          break;
        }
      }
      for (Predicate<CommandSender> requirement : requirements) {
        if (!requirement.test(sender)) {
          iter.remove();
          break;
        }
      }
    }
    isFiltered = true;
  }

  @Override
  public Audience filter(final Predicate<CommandSender> consumer) {
    viewers.removeIf(consumer);
    return this;
  }

  @Override
  public void forEach(Consumer<? super CommandSender> action) {
    if (!isFiltered) {
      applyGlobalFilter();
    }
    for (CommandSender v : viewers) {
      action.accept(v);
    }
  }

  @Override
  public Iterator<CommandSender> iterator() {
    if (!isFiltered) {
      applyGlobalFilter();
    }
    return viewers.iterator();
  }

  @Override
  public Spliterator<CommandSender> spliterator() {
    if (!isFiltered) {
      applyGlobalFilter();
    }
    return viewers.spliterator();
  }

  @Override
  public Set<CommandSender> toSet() {
    if (!isFiltered) {
      applyGlobalFilter();
    }
    return viewers;
  }

  @Override
  public List<CommandSender> toList() {
    if (!isFiltered) {
      applyGlobalFilter();
    }
    return List.copyOf(viewers);

  }

  @Override
  public CommandSender[] toArray() {
    if (!isFiltered) {
      applyGlobalFilter();
    }
    return viewers.toArray(new CommandSender[viewers.size()]);
  }

  @Override
  public String toString() {
    if (!isFiltered) {
      applyGlobalFilter();
    }
    return "Audience [viewers=" + viewers + "]";
  }

  @Override
  public int hashCode() {
    if (!isFiltered) {
      applyGlobalFilter();
    }
    return 31 + viewers.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Audience other = (Audience) obj;
    if (!isFiltered) {
      applyGlobalFilter();
    }
    return viewers.equals(other.toSet());
  }

  @Override
  public int compareTo(final IAudience<CommandSender> o) {
    if (!isFiltered) {
      applyGlobalFilter();
    }
    if (o == null) {
      return viewers.size();
    }
    return viewers.size() - o.toSet().size();
  }

  @Override
  public Audience clone() {
    return new Audience(viewers);
  }

  @Override
  public Audience addViewers(final CommandSender viewer) {
    viewers.add(viewer);
    return this;
  }

  @Override
  public Audience addViewers(final CommandSender... viewer) {
    viewers.addAll(List.of(viewer));
    isFiltered = false;
    return this;
  }

  @Override
  public Audience addViewers(final Collection<CommandSender> viewer) {
    viewers.addAll(viewer);
    isFiltered = false;
    return this;
  }

  @Override
  public Audience removeViewers(final CommandSender viewer) {
    viewers.remove(viewer);
    return this;
  }

  @Override
  public Audience removeViewers(final CommandSender... viewer) {
    viewers.removeAll(viewers);
    return this;
  }

  @Override
  public Audience removeViewers(final Collection<CommandSender> viewer) {
    viewers.removeAll(viewers);
    return this;
  }

  @Override
  public boolean hasViewer(final CommandSender viewer) {
    return viewers.contains(viewer);
  }

  @Override
  public Audience clearViewers() {
    viewers.clear();
    isFiltered = true;
    return this;
  }

  @Override
  public IAudience<CommandSender> withPermissions(final String permission) {
    permissions.add(permission);
    isFiltered = false;
    return this;
  }

  @Override
  public boolean hasPermissions(final String permission) {
    return permissions.contains(permission);
  }

  @Override
  public IAudience<CommandSender> removePermissions(final String permission) {
    permissions.remove(permission);
    return this;
  }

  @Override
  public IAudience<CommandSender> clearPermissions(final String permission) {
    permissions.clear();
    return this;
  }

  @Override
  public IAudience<CommandSender> withRequirements(final Predicate<CommandSender> permission) {
    requirements.add(permission);
    isFiltered = false;
    return this;
  }

  @Override
  public boolean hasRequirements(final Predicate<CommandSender> requirement) {
    return requirements.contains(requirement);
  }

  @Override
  public IAudience<CommandSender> removeRequirements(final Predicate<CommandSender> requirement) {
    requirements.remove(requirement);
    return this;
  }

  @Override
  public IAudience<CommandSender> clearRequirements(final Predicate<CommandSender> requirement) {
    requirements.clear();
    return this;
  }

  @Override
  public IAudience<CommandSender> difference(IAudience<CommandSender> audience) {
    viewers.removeAll(audience.toSet());
    return this;
  }

  @Override
  public IAudience<CommandSender> intersection(IAudience<CommandSender> audience) {
    viewers.retainAll(audience.toSet());
    return this;
  }

  @Override
  public IAudience<CommandSender> union(IAudience<CommandSender> audience) {
    viewers.addAll(audience.toSet());
    return this;
  }
}
