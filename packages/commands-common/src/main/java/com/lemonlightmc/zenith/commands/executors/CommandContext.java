package com.lemonlightmc.zenith.commands.executors;

import java.util.UUID;

public abstract class CommandContext<S> {
    protected static final UUID CONSOLE_UUID = UUID.nameUUIDFromBytes("zenith-console".getBytes());

    protected final String label;
    protected final RootCommand<?, S> cmd;
    protected final S sender;

    public CommandContext(final RootCommand<?, S> cmd, final String label, final S sender) {
        if (sender == null) {
            throw new IllegalArgumentException("CommandContext Sender cannot be null");
        }
        if (cmd == null) {
            throw new IllegalArgumentException("CommandContext Command cannot be null");
        }
        if (label == null || label.isEmpty() || !cmd.hasAlias(label)) {
            throw new IllegalArgumentException("CommandContext Label cannot be empty and be an aliases of the cmd");
        }
        this.label = label;
        this.cmd = cmd;
        this.sender = sender;
    }

    /**
     * Returns the underlying sender associated with this context.
     *
     * @return the command sender
     */
    public S sender() {
        return sender;
    }

    public String label() {
        return label;
    }

    public RootCommand<?, S> rootCommand() {
        return cmd;
    }

    public abstract boolean hasPermission(final String perm);

    /**
     * Creates a copy of this source for a different sender while preserving
     * location and entity context.
     *
     * @param <T>       the new sender type
     * @param newSender the sender to associate with the copied context
     * @return a copied context for the new sender
     */
    public abstract <T extends S> CommandContext<T> copyFor(final T newSender);

    /**
     * Creates a copy of this context.
     *
     * @return a copied context
     */
    public abstract CommandContext<S> copy();

    /**
     * Sends a message to the sender.
     *
     * @param str the message to send
     */
    public abstract void sendMessage(String message);

    public void sendMessage(String message, final String remaining, final String delay, final String cost,
            final String balance) {
        if (message == null || message.isBlank()) {
            return;
        }
        if (label != null) {
            message = message.replace("{command}", label);
        }
        if (remaining != null) {
            message = message.replace("{remaining}", remaining);
        }
        if (delay != null) {
            message = message.replace("{delay}", delay);
        }
        if (cost != null) {
            message = message.replace("{cost}", cost);
        }
        if (balance != null) {
            message = message.replace("{balance}", balance);
        }
        sendMessage(message);
    }

    /**
     * Sends a command error message to the underlying sender.
     *
     * @param e the command exception describing the error
     */
    public void sendError(final Exception e) {
        sendMessage(e.getMessage());
    }

    protected String formatMessage(String message) {
        if (message == null) {
            return message;
        }
        if (label != null) {
            message = message.replace("{command}", label);
        }
        return message;
    }

}
