package enviroment;

import enviroment.commands.Commands;

import java.io.Serializable;

public class Request implements Serializable {
    private final Commands command;

    public Request(Commands command) {
        this.command = command;
    }

    public Commands getCommand() {
        return command;
    }
}
