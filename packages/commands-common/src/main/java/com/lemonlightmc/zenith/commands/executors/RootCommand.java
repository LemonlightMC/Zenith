
package com.lemonlightmc.zenith.commands.executors;

import java.util.List;

import org.bukkit.NamespacedKey;

import com.lemonlightmc.zenith.commands.executors.RootCommand;

public abstract class RootCommand<T extends RootCommand<T, S>, S> extends AbstractCommand<T, S> {

  @Override
  public abstract T getInstance();

  public abstract T register();

  public abstract T unregister();

  public abstract boolean isRegistered();

  public abstract String getNamespace();

  public abstract String getKey();

  public abstract NamespacedKey getName();

  public abstract T setName(NamespacedKey key);

  public abstract T setKey(String key);

  public abstract T setNamespacey(String namespace);

  public abstract T withUsage(String usage);

  public abstract T withUsage(String... usage);

  public abstract T withUsage(List<String> usage);

  public abstract String[] getUsage();

  public abstract T withHelp(String shortDesc, String fullDesc);

  public abstract T withHelp(List<String> help);

  public abstract T withHelp(String help);

  public abstract List<String> getHelp();
}