import processing.core.PApplet;
import processing.core.PShape;
import queasycam.QueasyCam;

class FlameThrower {
    PApplet parent;
    PShape object;
    FireParticleSystem fireParticleSystem;

    public FlameThrower(PApplet parent, Vector3D origin, Vector3D aim, int generationRate, int lifeSpan, int maxParticles, PShape object) {
        this.parent = parent;
        this.object = object;
        fireParticleSystem = new FireParticleSystem(parent, origin, aim, generationRate, lifeSpan, maxParticles);
    }

    public void physics(float dt) {
        fireParticleSystem.physics(dt);
    }

    public void render() {
        parent.pushMatrix();
        parent.translate(300, 10, 180);
        parent.fill(0, 0, 255);
        parent.shape(object);
        parent.popMatrix();
        fireParticleSystem.render();
    }
}

public class Fire extends PApplet {
    final int WIDTH = 1500;
    final int HEIGHT = 1000;
    QueasyCam cam;
    Ground ground;
    PShape tree;
    FlameThrower flameThrower;

    @Override
    public void settings() {
        size(WIDTH, HEIGHT, P3D);
    }

    @Override
    public void setup() {
        surface.setTitle("Processing");
        noStroke();
        // camera
        cam = new QueasyCam(this);
        cam.sensitivity = 1f;
        cam.speed = 2f;
        // ground
        ground = new Ground(this,
                Vector3D.of(0, 0, 0), Vector3D.of(0, 0, 1), Vector3D.of(1, 0, 0),
                1024, 1024,
                loadImage("grass.jpg"));
        // tree
        tree = loadShape("BirchTree_Autumn_1.obj");
        tree.rotate(PI, 0, 0, 1);
        tree.scale(60);
        // flame thrower
        PShape flameThrowerObj = loadShape("LongPistol.obj");
        flameThrowerObj.scale(10);
        flameThrowerObj.rotate(PI, 0, 0, 1);
        flameThrowerObj.setStroke(color(255));
        flameThrowerObj.setFill(color(0, 0, 128));
        flameThrower = new FlameThrower(this,
                Vector3D.of(300, 0, 150), Vector3D.of(0, 0, -1),
                100, 200, 35000, flameThrowerObj);
    }

    @Override
    public void draw() {
        if (keyPressed && keyCode == RIGHT) {
            flameThrower.fireParticleSystem.origin = flameThrower.fireParticleSystem.origin.plus(Vector3D.of(0, 0, 1));
        } else if (keyPressed && keyCode == LEFT) {
            flameThrower.fireParticleSystem.origin = flameThrower.fireParticleSystem.origin.plus(Vector3D.of(0, 0, -1));
        } else if (keyPressed && keyCode == UP) {
            flameThrower.fireParticleSystem.origin = flameThrower.fireParticleSystem.origin.plus(Vector3D.of(1, 0, 1));
        } else if (keyPressed && keyCode == DOWN) {
            flameThrower.fireParticleSystem.origin = flameThrower.fireParticleSystem.origin.plus(Vector3D.of(-1d, 0, 0));
        }

        // background
        background(220);
        // ground and tree
        pushMatrix();
        translate(300, 100, 0);
        ground.render();
        shape(tree);
        popMatrix();

        int frameStart = millis();
        // flamethrower physics
        flameThrower.physics(0.015f);
        int physicsEnd = millis();
        // flamethrower rendering
        flameThrower.render();
        int frameEnd = millis();
        // text overlay
        surface.setTitle("Processing"
                + " FPS: " + round(frameRate)
                + " Phy: " + round(physicsEnd - frameStart) + "ms"
                + " Ren: " + round(frameEnd - physicsEnd) + "ms"
                + " #par: " + flameThrower.fireParticleSystem.particles.size()
        );
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
