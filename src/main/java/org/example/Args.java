package org.example;

import java.util.*;

public class Args {

    private record Triplet(String first, String second, String third) {
        boolean matchAny(String str) {
            var reduced = str;//.replace("-", "");
            return reduced.equals(first) || reduced.equals(second);
        }
    }

    public static class ArgsBuilder {
        private Set<Triplet> commands = new HashSet<>();
        private Set<Triplet> options = new HashSet<>();

        public ArgsBuilder addCommand(String cmd, String longCmd, String help) {
            commands.add(new Triplet(cmd, longCmd, help));
            return this;
        }

        public ArgsBuilder addOption(String prop, String longProp, String help) {
            options.add(new Triplet(prop, longProp, help));
            return this;
        }

        public Args build() {
            return new Args(commands, options);
        }

    }

    private final Set<Triplet> commands;
    private final Set<Triplet> options;

    private Map<String, String> parsedOptions = new HashMap<>();
    private List<String> parsedCommands = new ArrayList<>();

    private List<String> arguments = new ArrayList<>();

    private Args(Set<Triplet> commands, Set<Triplet> oprions) {
        this.commands = commands;
        this.options = oprions;
    }

    public void parse(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--") || args[i].startsWith("-")) {
                var opt = args[i];//.replace("-", "");
                var n = ++i;
                options.stream().filter(
                        o -> o.matchAny(opt))
                        .findAny()
                        .ifPresent(trip -> parsedOptions.put(trip.second.replace("-", ""), args[n]));
            } else {
                var n = i;
                commands.stream()
                        .filter(cmd -> cmd.matchAny(args[n]))
                        .findAny()
                        .ifPresentOrElse(
                                trip -> parsedCommands.add(trip.second),
                                () -> arguments.add(args[n])
                        );
            }
        }
    }

    public boolean isCommandPresent(String cmd) {
        return parsedCommands.contains(cmd);
    }

    public boolean isOptionPresent(String opt) {
        return parsedOptions.containsKey(opt);
    }

    public String getOptionValue(String opt) {
        return parsedOptions.get(opt);
    }

    public List<String> getArguments() {
        return arguments;
    }

}
