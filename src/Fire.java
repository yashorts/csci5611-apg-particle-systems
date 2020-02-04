import processing.core.PApplet;
import processing.core.PShape;
import queasycam.QueasyCam;

public class Fire extends PApplet {
    final int WIDTH = 1500;
    final int HEIGHT = 1000;
    QueasyCam cam;
    Ground ground;
    PShape tree;
    PShape flameThrower;
    FireParticleSystem ps;

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
        // flamethrower
        flameThrower = loadShape("LongPistol.obj");
        flameThrower.scale(10);
        flameThrower.rotate(PI, 0, 0, 1);
        flameThrower.setStroke(color(255));
        flameThrower.setFill(color(128, 0, 0));
        ps = new FireParticleSystem(this,
                Vector3D.of(300, 0, 150), Vector3D.of(0, 0, -1),
                100, 200, 35000);
    }

    @Override
    public void draw() {
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
        ps.physics(0.015f);
        int physicsEnd = millis();
        // flamethrower rendering
        pushMatrix();
        translate(300, 10, 180);
        shape(flameThrower);
        popMatrix();
        ps.render();
        int frameEnd = millis();
        // text overlay
        surface.setTitle("Processing"
                + " FPS: " + round(frameRate)
                + " Phy: " + round(physicsEnd - frameStart) + "ms"
                + " Ren: " + round(frameEnd - physicsEnd) + "ms"
                + " #par: " + ps.particles.size()
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