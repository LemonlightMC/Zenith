package com.lemonlightmc.zenith.commands.argumentsbase;

public enum ArgumentType {

  ADVANCEMENT("Advancement"),

  ADVENTURE_CHAT("AdventureChat"), // wont implemented
  ADVENTURE_CHATCOLOR("AdventureChatColor"), // wont implemented
  ADVENTURE_CHAT_COMPONENT("AdventureChatComponent"), // wont implemented

  ANGLE("Angle"), // wont implemented
  ROTATION("Rotation"),

  AXIS("Axis"),

  BLOCK_PREDICATE("BlockPredicate"), // Not implemented
  BLOCKSTATE("BlockState"),
  BLOCKDATA("BlockData"),
  MATERIAL("Material"),

  CHAT("Chat"), // Not implemented
  CHAT_COMPONENT("ChatComponent"), // Not implemented
  CHATCOLOR("ChatColor"),

  ENTITY_SELECTOR("EntitySelector"),
  ENTITY_TYPE("EntityType"),

  ITEMSTACK("ItemStack"),
  ITEMSTACK_PREDICATE("ItemStackPredicate"), // Not implemented

  NAMESPACED_KEY("NamespacedKey"),
  UUID("UUID"),

  PLAYER("Player"),
  PLAYERPROFILE("PlayerProfile"),
  OFFLINE_PLAYER("OfflinePlayer"),

  BOOLEAN("Boolean"),
  DOUBLE("Double"),
  FLOAT("Float"),
  INTEGER("Integer"),
  LONG("Long"),
  STRING_GREEDY("GreedyString"),
  STRING("String"),
  TEXT("Text"),

  RANGE_INT("IntegerRange"),
  RANGE_LONG("LongRange"),
  RANGE_FLOAT("FloatRange"),
  RANGE_DOUBLE("DoubleRange"),

  LIST_GREEDY("GreedyList"),
  LIST_TEXT("TextList"),
  FUNCTION("Function"), // wont implemented
  MAP("Map"),
  NBT_COMPOUND("NbtCompound"), // wont implemented

  MATH_OPERATION("MathOperation"),
  OBJECTIVE("Objective"),
  CRITERIA("Criteria"),
  SCORE_HOLDER("ScoreHolder"), // wont implemented
  SCOREBOARD_SLOT("ScoreboardSlot"),

  SOUND("Sound"),
  PARTICLE("Particle"), // unfinished implemented
  ENCHANTMENT("Enchantment"),
  LOOT_TABLE("LootTable"),
  RECIPE("Recipe"),
  POTION_EFFECT("PotionEffect"),

  TEAM("Team"),

  LOCATION("Location"),
  LOCATION_2D("Location2D"),
  TIME("Time"),
  BIOME("Biome"),
  WORLD("World"),
  ENVIRONMENT("Environment"),
  DIMENSION("Dimension"), // wont implemented
  LOOK_ANCHOR("LookAnchor"),
  GAME_MODE("GameMode"),
  STRUCTURE("Structure"),
  STRUCTURE_TYPE("StructureType"),
  ATTRIBUTE("Attribute"),

  COMMAND("Command"),
  CUSTOM("Custom"), // Not implemented
  FLAG("Flag"),
  SWITCH("Switch"),
  LITERAL("Literal"),
  MULTI_LITERAL("MultiLiteral"),
  BRANCH("Branch");

  private final String name;

  private ArgumentType(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public boolean isPrimitive() {
    return this.name().startsWith("PRIMITIVE_");
  }

  public boolean isList() {
    return this.name().startsWith("LIST_");
  }

  public boolean isLiteral() {
    return this == LITERAL || this == MULTI_LITERAL;
  }

  public boolean isGreedy() {
    return this == STRING_GREEDY || this == LIST_GREEDY;
  }
}
