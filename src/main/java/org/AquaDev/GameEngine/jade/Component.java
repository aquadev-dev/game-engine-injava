package org.AquaDev.GameEngine.jade;

public abstract class Component {

    public GameObject gameObject = null;

    public void start() {

    }

    public abstract void update(float dt);

}
