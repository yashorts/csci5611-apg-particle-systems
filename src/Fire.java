import processing.core.PApplet;
import processing.core.PShape;

import queasycam.*;

public class Fire extends PApplet {
    QueasyCam cam;
    FireParticleSystem ps;
    PShape tree;

    @Override
    public void settings() {
        size(1000, 700, P3D);
    }

    @Override
    public void setup() {
        ps = new FireParticleSystem(this, 1);
        noStroke();
        cam = new QueasyCam(this);
        surface.setTitle("Processing");
        tree = loadShape("BirchTree_1.obj");
        tree.rotate(3.14f, 0.0f, 0.0f, 1.0f);
        tree.rotate(3.14f, 0.0f, 1.0f, 0.0f);
    }

    @Override
    public void draw() {
        int frameStart = millis();
        // physics
        ps.update();
        int physicsEnd = millis();
        // rendering
        background(255);
        lights();
        shape(tree, width * 0.5f, height * 0.6f, 200, 200);
        fill(255, 0, 0);
        ps.render();
        int frameEnd = millis();
        // text overlay
        surface.setTitle("Processing"
                + " FPS: " + round(frameRate)
                + " Phy: " + round(physicsEnd - frameStart) + "ms"
                + " Ren: " + round(frameEnd - physicsEnd) + "ms");
    }

    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[]{"Fire"};
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }
}