package enviroment.commands;

import clientmod.InputManager;

public interface CommandWrap {
    Commands create(String arg, InputManager inputManager) throws Exception;
}
