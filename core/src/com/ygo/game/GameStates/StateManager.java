package com.ygo.game.GameStates;

import java.util.Stack;

/**
 * Created by semahbod on 10/8/16.
 */
public class StateManager {

    private static Stack<GameState> states = new Stack<GameState>();

    public static void pushState(GameState state) {
        states.push(state);
        state.show();
    }

    public static void popState() {
        states.pop().dispose();
    }

    public static GameState getCurrentState() {
        return states.peek();
    }

    public static void update(float dt) {
        states.peek().update(dt);
    }

    public static void render() {
        states.peek().render();
    }

    public static void resize(int width, int height) {
        states.peek().resize(width, height);
    }

    public static void pause() {
        states.peek().pause();
    }

    public static void resume() {
        states.peek().resume();
    }

    public static void dispose() {
        while (!states.empty()) {
            states.pop().dispose();
        }
    }
}
