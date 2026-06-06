package com.lemonlightmc.zenith.modular;

import java.util.List;

import com.lemonlightmc.zenith.events.EventsAPI;
import com.lemonlightmc.zenith.modular.ModuleAPI.ModuleDisableEvent;
import com.lemonlightmc.zenith.modular.ModuleAPI.ModuleEnableEvent;
import com.lemonlightmc.zenith.modular.ModuleAPI.ModuleRegisterEvent;
import com.lemonlightmc.zenith.modular.ModuleAPI.ModuleUnregisterEvent;
import com.lemonlightmc.zenith.version.Version;

public abstract class Module {
  protected final String key;
  protected boolean isEnabled = false;
  protected boolean isRegistered = false;

  protected Version version;
  protected List<String> depends;
  protected List<String> softDepends;

  public Module(final String key, final Version version, final List<String> depends,
      final List<String> softDepends) {
    if (key == null) {
      throw new IllegalArgumentException("Module Key cant be null!");
    }
    this.key = key;
    this.version = version == null ? Version.FIRST_VERSION : version;
    this.depends = depends == null ? List.of() : depends;
    this.softDepends = softDepends == null ? List.of() : softDepends;
  }

  public Module(final String key, final Version version, final List<String> depends) {
    this(key, version, depends, null);
  }

  public Module(final String key, final Version version) {
    this(key, version, null, null);
  }

  public Module(final String key) {
    this(key, null, null, null);
  }

  public String getKey() {
    return key;
  }

  public Version getVersion() {
    return version;
  }

  public List<String> getDepends() {
    return depends;
  }

  public List<String> getSoftDepends() {
    return softDepends;
  }

  public boolean isRegistered() {
    return isRegistered;
  }

  public boolean isEnabled() {
    return isEnabled;
  }

  public Module register() {
    if (isRegistered) {
      return this;
    }
    ModuleAPI.register(this);
    isRegistered = true;
    onRegister();
    EventsAPI.call(new ModuleRegisterEvent(this));
    return this;
  }

  public Module unregister() {
    if (!isRegistered) {
      return this;
    }
    ModuleAPI.unregister(this);
    onUnregister();
    isRegistered = false;
    EventsAPI.call(new ModuleUnregisterEvent(this));
    return this;
  }

  public void enable() {
    if (!isRegistered) {
      throw new IllegalStateException("Module '" + key + "' is not registered!");
    }
    if (isEnabled) {
      return;
    }
    this.isEnabled = true;
    onEnable();

    EventsAPI.call(new ModuleEnableEvent(this));
  }

  public void disable() {
    if (!isRegistered) {
      throw new IllegalStateException("Module '" + key + "' is not registered!");
    }
    if (!isEnabled) {
      return;
    }
    EventsAPI.call(new ModuleDisableEvent(this));
    this.isEnabled = false;
    disable();
  }

  public void reload() {
  }

  public void onRegister() {
  }

  public void onUnregister() {
  }

  public void onReload() {

  }

  public void onEnable() {

  }

  public void onDisable() {
  }

  @Override
  public int hashCode() {
    int result = 31 + key.hashCode();
    result = 31 * result + depends.hashCode();
    result = 31 * result + softDepends.hashCode();
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Module other = (Module) obj;
    return key.equals(other.key) && depends.equals(other.depends) && softDepends.equals(other.softDepends);
  }

  @Override
  public String toString() {
    return "Module [key=" + key + ", isEnabled=" + isEnabled + ", depends=" + depends + ", softDepends=" + softDepends
        + "]";
  }

}
