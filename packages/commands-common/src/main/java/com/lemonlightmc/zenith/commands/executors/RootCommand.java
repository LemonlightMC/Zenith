
package com.lemonlightmc.zenith.commands.executors;

import java.util.List;

import org.bukkit.NamespacedKey;

import com.lemonlightmc.zenith.data.KeyedBukkit;
import com.lemonlightmc.zenith.data.Namespaced;

public abstract class RootCommand<T extends RootCommand<T, S>, S> extends AbstractCommand<T, S>
    implements Namespaced, KeyedBukkit {

  public abstract T register();

  public abstract T unregister();

  public abstract boolean isRegistered();

  public abstract String name();

  public abstract T setName(String key);

  public abstract T setKey(NamespacedKey key);

  public abstract T setNamespace(String namespace);

  public abstract T withUsage(String usage);

  public abstract T withUsage(String... usage);

  public abstract T withUsage(List<String> usage);

  public abstract String[] usage();

  public abstract T withHelp(String shortDesc, String fullDesc);

  public abstract T withHelp(List<String> help);

  public abstract T withHelp(String help);

  public abstract List<String> help();
}