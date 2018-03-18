package org.anhcraft.spaciouslib.command;

import org.anhcraft.spaciouslib.utils.RegEx;
import org.anhcraft.spaciouslib.utils.Strings;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * A subcommand is one or many static arguments of a command (which created by SCommand)<br>
 * A subcommand (static argument) can have unlimited dynamic arguments<br>
 * With a dynamic argument, players can type anything they want but it needs to depend on the type of that argument<br>
 * E.g: players can only type integer number if the type of that argument which they was typed in is INTEGER_NUMBER
 */
public class SubCommand extends CommandString{
    private String name;
    private String description;
    private CommandRunnable rootRunnable;
    private LinkedHashMap<CommandArgument.Type, String> argErrorMessages = new LinkedHashMap<>();
    private LinkedHashMap<CommandArgument, CommandArgument.Type> args = new LinkedHashMap<>();
    private String doesNotEnoughtArgsErrorMessage;
    protected String canNotFindCmdErrorMessage;
    private boolean hideTypeCommandString = false;

    /**
     * Creates a new SubCommand instance
     * @param name the name of this subcommand
     * @param description the description of this subcommand
     * @param rootRunnable a runnable which was triggered if a player run this subcommand
     * @throws Exception
     */
    public SubCommand(String name, String description, CommandRunnable rootRunnable) throws Exception {
        this.name = name.trim().toLowerCase();
        this.rootRunnable = rootRunnable;
        this.description = description;
        init();
    }

    private void init() {
        setArgErrorMessage(CommandArgument.Type.URL,
                "&cYou must type a valid URL! (e.g: https://example.com/)");
        setArgErrorMessage(CommandArgument.Type.EMAIL,
                "&cYou must type a valid email address! (e.g: email@website.com)");
        setArgErrorMessage(CommandArgument.Type.ONLINE_PLAYER,
                "&cThat player isn't online!");
        setArgErrorMessage(CommandArgument.Type.INTEGER_NUMBER,
                "&cYou must type a valid integer number! (e.g: 1, 5, 10, -3, etc)");
        setArgErrorMessage(CommandArgument.Type.REAL_NUMBER,
                "&cYou must type a valid real number! (e.g: 0.1, 5, -3.2, 49.0, etc)");
        setArgErrorMessage(CommandArgument.Type.BOOLEAN,
                "&cYou must type a valid boolean! (true, false)");
        setArgErrorMessage(CommandArgument.Type.WORLD,
                "&cCouldn't find that world!");
        setDoesNotEnoughtArgsErrorMessage("&cNot enough arguments!");
        setCanNotFindCmdMessage("&cCan't find that command. Please recheck the syntax.");
    }

    public String getName(){
        return this.name;
    }

    public CommandArgument.Type getArgumentType(CommandArgument arg){
        return this.args.get(arg);
    }

    public ArrayList<CommandArgument> getArguments(){
        return new ArrayList<>(this.args.keySet());
    }

    public SubCommand hideTypeCommandString(){
        this.hideTypeCommandString = true;
        return this;
    }

    public ArrayList<CommandArgument> getArguments(boolean optional){
        ArrayList<CommandArgument> x = new ArrayList<>();
        for(CommandArgument c : getArguments()){
            if(c.isOptional() == optional){
                x.add(c);
            }
        }
        return x;
    }

    public SubCommand removeArgument(CommandArgument arg){
        this.args.remove(arg);
        return this;
    }

    /**
     * Creates a new dynamic argument for this subcommand
     * @param arg CommandArgument instance
     * @param type type of this argument
     * @return
     */
    public SubCommand setArgument(CommandArgument arg, CommandArgument.Type type){
        this.args.put(arg, type);
        return this;
    }

    /**
     * Creates a new dynamic argument for this subcommand
     * @param name the name of this argument
     * @param argRunnable runnable for this argument, only triggers if a player runs a command which has this argument at the end
     * @param type the type of this argument
     * @param optional
     * @return
     * @throws Exception
     */
    public SubCommand setArgument(String name, CommandRunnable argRunnable, CommandArgument.Type type, boolean optional) throws Exception {
        setArgument(new CommandArgument(name, argRunnable, optional), type);
        return this;
    }

    public SubCommand setArgErrorMessage(CommandArgument.Type type, String message){
        this.argErrorMessages.put(type, Strings.color(message));
        return this;
    }

    public SubCommand setDoesNotEnoughtArgsErrorMessage(String message){
        this.doesNotEnoughtArgsErrorMessage = Strings.color(message);
        return this;
    }

    public SubCommand setCanNotFindCmdMessage(String message){
        this.canNotFindCmdErrorMessage = Strings.color(message);
        return this;
    }

    protected String getCommandString(boolean color){
        StringBuilder cmd;
        if(color){
            cmd = new StringBuilder((color ? gcs(Type.BEGIN_SUB_COMMAND) : "") + this.name);
            for(CommandArgument arg : args.keySet()){
                cmd = cmd.append(" ").append(arg.isOptional() ? gcs(Type.BEGIN_ARGUMENT_OPTIONAL) : gcs(Type.BEGIN_ARGUMENT))
                        .append(arg.isOptional() ? gcs(Type.ARGUMENT_NAME_OPTIONAL) : gcs(Type.ARGUMENT_NAME))
                        .append(arg.getName());
                if(!hideTypeCommandString){
                    cmd = cmd.append(arg.isOptional() ? gcs(Type.ARGUMENT_TYPE_OPTIONAL) : gcs(Type.ARGUMENT_TYPE))
                            .append(args.get(arg).toString());
                }
                cmd = cmd.append(arg.isOptional() ? gcs(Type.END_ARGUMENT_OPTIONAL) : gcs(Type.END_ARGUMENT));
            }
            if(description != null){
                cmd = cmd.append(gcs(Type.DESCRIPTION)).append(" ").append(description);
            }
        } else {
            cmd = new StringBuilder(this.name);
            for(CommandArgument arg : args.keySet()){
                cmd = cmd.append(" ").append(arg.getName());
                if(!hideTypeCommandString){
                    cmd = cmd.append(args.get(arg).toString());
                }
            }
            if(description != null) {
                cmd = cmd.append(" ").append(description);
            }
        }
        return cmd.toString();
    }

    /**
     * Normalizes this subcommand<br>
     * It'll sort optional arguments into the end of this subcommand. The order between optional arguments or non-optional arguments won't be change.
     * @return
     */
    public SubCommand normalize(){
        LinkedHashMap<CommandArgument, CommandArgument.Type> require = new LinkedHashMap<>();
        LinkedHashMap<CommandArgument, CommandArgument.Type> optional = new LinkedHashMap<>();
        for(CommandArgument c : getArguments()){
            if(c.isOptional()) {
                continue;
            }
            require.put(c, getArgumentType(c));
        }
        for(CommandArgument c : getArguments()){
            if(!c.isOptional()) {
                continue;
            }
            optional.put(c, getArgumentType(c));
        }
        this.args = new LinkedHashMap<>();
        this.args.putAll(require);
        this.args.putAll(optional);
        return this;
    }

    /**
     * Checks is this subcommand valid
     * @return
     */
    public boolean isValid(){
        int a = 0;
        for(CommandArgument c : getArguments()){
            if(c.isOptional()) {
                a = 1;
            } else {
                if(a == 1){
                  return false;
                }
            }
        }
        return true;
    }

    protected void execute(SCommand cmd, CommandSender s, String[] a) {
        SubCommand sc = this;
        if(a.length == 0){
            rootRunnable.run(cmd, sc, s, a, "");
        } else {
            LinkedHashMap<CommandArgument, String> values = new LinkedHashMap<>();
            int i = 0;
            for(String value : a) {
                if(getArguments().size() <= i) {
                    break;
                }
                values.put(getArguments().get(i), value);
                i++;
            }
            if(values.size() < getArguments(false).size()) {
                s.sendMessage(sc.doesNotEnoughtArgsErrorMessage);
            } else {
                boolean hasError = false;
                argTypeValidator:
                for(CommandArgument arg : values.keySet()) {
                    String value = values.get(arg);
                    switch(getArgumentType(arg)) {
                        case URL:
                            if(!RegEx.URL.matches(value)) {
                                s.sendMessage(sc.argErrorMessages.get(CommandArgument.Type.URL));
                                hasError = true;
                                break argTypeValidator;
                            }
                            break;
                        case EMAIL:
                            if(!RegEx.EMAIL.matches(value)) {
                                s.sendMessage(sc.argErrorMessages.get(CommandArgument.Type.EMAIL));
                                hasError = true;
                                break argTypeValidator;
                            }
                            break;
                        case BOOLEAN:
                            if(!value.equalsIgnoreCase("true")
                                    && !value.equalsIgnoreCase("false")) {
                                s.sendMessage(sc.argErrorMessages.get(CommandArgument.Type.BOOLEAN));
                                hasError = true;
                                break argTypeValidator;
                            }
                            break;
                        case WORLD:
                            if(Bukkit.getServer().getWorld(value) == null) {
                                s.sendMessage(sc.argErrorMessages.get(CommandArgument.Type.WORLD));
                                hasError = true;
                                break argTypeValidator;
                            }
                            break;
                        case REAL_NUMBER:
                            if(!RegEx.REAL_NUMBER.matches(value)) {
                                s.sendMessage(sc.argErrorMessages.get(CommandArgument.Type.REAL_NUMBER));
                                hasError = true;
                                break argTypeValidator;
                            }
                            break;
                        case INTEGER_NUMBER:
                            if(!RegEx.INTEGER_NUMBER.matches(value)) {
                                s.sendMessage(sc.argErrorMessages.get(CommandArgument.Type.INTEGER_NUMBER));
                                hasError = true;
                                break argTypeValidator;
                            }
                            break;
                        case ONLINE_PLAYER:
                            if(!Bukkit.getServer().getOfflinePlayer(value).isOnline()) {
                                s.sendMessage(sc.argErrorMessages.get(CommandArgument.Type.ONLINE_PLAYER));
                                hasError = true;
                                break argTypeValidator;
                            }
                            break;
                    }
                }
                if(!hasError) {
                    if((values.size() - 1) >= 0 && (values.size() - 1) < values.size()) {
                        CommandArgument arg = new ArrayList<>(values.keySet())
                                .get(values.size() - 1);
                        arg.getRunnable().run(cmd, sc, s, a, values.get(arg));
                    } else {
                        s.sendMessage(sc.canNotFindCmdErrorMessage);
                    }
                }
            }
        }
    }
}
