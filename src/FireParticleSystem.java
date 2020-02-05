import processing.core.PApplet;
import processing.core.PImage;

import java.util.*;


public class FireParticleSystem {
    // window
    final PApplet parent;
    // pose
    Vec3 origin;
    Vec3 aim;
    // flow
    final int maxGenerationRate;
    int generationRate;
    final int lifespan;
    final int maxParticles;
    List<FireParticle> particles = new ArrayList<>();
    private List<Integer> deadParticleIndices = new ArrayList<>();
    // texture
    static List<String> textureFiles = new ArrayList<>();
    PImage texture;

    static {
        textureFiles.add("fire-yellow-1.jpg");
        textureFiles.add("fire-red-4.jpg");
        textureFiles.add("fire-red-5.jpg");
        textureFiles.add("fire-red-6.png");
    }

    FireParticleSystem(PApplet parent, Vec3 origin, Vec3 aim, int generationRate, int lifespan, int maxParticles) {
        this.parent = parent;
        this.origin = origin;
        this.aim = aim;
        this.maxGenerationRate = generationRate;
        this.generationRate = 0;
        this.lifespan = lifespan;
        this.maxParticles = maxParticles;
        Collections.shuffle(textureFiles);
        this.texture = parent.loadImage(textureFiles.get(0));
    }

    public void physics(float dt) {
        // add new particles
        for (int i = 0; i < generationRate; ++i) {
            if (particles.size() >= maxParticles) {
                break;
            }
            Vec3 sphereRandomness = Vec3.uniformRandomInUnitSphere();
            Vec3 generalVelocity = aim.scale(50f * generationRate / maxGenerationRate);
            Vec3 acc = Vec3.of(0, 0, 0);
            particles.add(new FireParticle(
                    parent,
                    origin.plus(sphereRandomness),
                    generalVelocity,
                    acc.plus(sphereRandomness.scale(2)),
                    lifespan,
                    texture
            ));
        }
        // remove dead particles, update states of live particles
        deadParticleIndices.clear();
        for (int i = 0; i < particles.size(); ++i) {
            FireParticle p = particles.get(i);
            if (p.stage == Stage.DEAD) {
                deadParticleIndices.add(i);
                continue;
            }
            p.physics(dt);
        }
        for (int i = deadParticleIndices.size() - 1; i >= 0; --i) {
            int index = deadParticleIndices.get(i);
            particles.remove(index);
        }
    }

    public void render() {
        for (int i = 0; i < particles.size(); ++i) {
            FireParticle p = particles.get(i);
            p.render();
        }
    }

    public void incrementGenRate(int dg) {
        generationRate = Math.min(generationRate + dg, maxGenerationRate);
    }

    public void decrementGenRate(int dg) {
        generationRate = Math.max(generationRate - dg, 0);
    }

}
