import processing.core.PApplet;
import processing.core.PImage;

enum Stage {
    INIT, JET, JET_TO_BALL_OR_SMOG, BALL, SMOG, SMOKE, DEAD
}

enum Shape {
    POINT, BOX
}

public class FireParticle {
    final PApplet parent;
    Vec3 position;
    Vec3 velocity;
    Vec3 initialShootDir;
    Vec3 acceleration;
    Vec3 color;
    float totalLifeSpan;
    int remainingLifespan;
    Stage stage;
    Shape shape;
    final PImage texture;

    FireParticle(PApplet parent, Vec3 position, Vec3 velocity, Vec3 acceleration, int remainingLifespan, PImage texture) {
        this.parent = parent;
        this.position = position;
        this.velocity = velocity;
        this.initialShootDir = velocity.unit();
        this.acceleration = acceleration;
        this.stage = Stage.INIT;
        this.shape = Shape.POINT;
        this.remainingLifespan = remainingLifespan;
        this.totalLifeSpan = remainingLifespan;
        this.texture = texture;
        this.color = Vec3.of(0, 255, 0);
    }

    public void physics(float dt) {
        switch (stage) {
            case INIT:
                if (parent.random(1) < 0.005) {
                    shape = Shape.BOX;
                }
                stage = Stage.JET;
                color = gradientColor();
                break;
            case JET:
                position = position.plus(velocity.scale(dt)).plus(Vec3.uniformRandomInUnitSphere().scale(0.1f));
                velocity = velocity.plus(acceleration.scale(dt));
                if (parent.random(1) < 0.05) {
                    acceleration = Vec3.uniformRandomInUnitSphere().scale(5f).plus(initialShootDir.scale(10));
                }
                if (parent.random(1) < 0.001) {
                    acceleration = acceleration.plus(initialShootDir.scale(50));
                }
                color = gradientColor();
                // very small portion of initial particles turning into smog
                if (parent.random(1) < 0.0001) {
                    changeStageToSmog();
                }
                // jet stage ends after some lifespan
                if (remainingLifespan / totalLifeSpan <= 0.6) {
                    stage = Stage.JET_TO_BALL_OR_SMOG;
                }
                break;
            case JET_TO_BALL_OR_SMOG:
                position = position.plus(velocity.scale(dt)).plus(Vec3.uniformRandomInUnitSphere().scale(0.1f));
                velocity = velocity.plus(acceleration.scale(dt));
                color = gradientColor();
                if (parent.random(1) < (0.54 - remainingLifespan / totalLifeSpan)) {
                    changeStageToSmog();
                } else {
//                    float theta = parent.random(2 * parent.PI);
//                    float radius = 0.5f * (float) Math.sqrt(parent.random(1));
//                    Vector3D coneRandomness = Vector3D.of(radius * Math.cos(theta), radius * Math.sin(theta), 0).minus(Vector3D.of(0, 0, 1)).scale(20);
//                    velocity = velocity.plus(coneRandomness);
                    stage = Stage.BALL;
                    if (parent.random(1) < 0.1) {
                        shape = Shape.BOX;
                    }
                }
                break;
            case BALL:
                position = position.plus(velocity.scale(dt)).plus(Vec3.uniformRandomInUnitSphere().scale(0.5f));
                velocity = velocity.plus(acceleration.scale(dt));
                acceleration = acceleration.plus(Vec3.of(parent.random(-5, 5), -0.5, 0));
                if (parent.random(1) < 0.05) {
                    color = Vec3.of(0);
                } else {
                    color = gradientColor();
                }
                // small portion of ball particles turning into smog
                if (parent.random(1) < 0.002) {
                    changeStageToSmog();
                }
                break;
            case SMOG:
                position = position.plus(velocity.scale(dt)).plus(Vec3.uniformRandomInUnitSphere().scale((float) Math.pow((2 * (1 - remainingLifespan / totalLifeSpan)), 3)));
                velocity = velocity.plus(acceleration.scale(dt));
                acceleration = acceleration.plus(Vec3.of(parent.random(-1, 1), parent.random(-5, 2), 0));
                color = Vec3.of(parent.random(100, 200 + 55 * (1 - remainingLifespan / totalLifeSpan)));
                break;
            case SMOKE:
                break;
            case DEAD:
                return;
            default:
                return;
        }
        remainingLifespan -= 1;
        if (remainingLifespan <= 0) {
            stage = Stage.DEAD;
        }
    }

    public void render() {
        if (shape == Shape.BOX) {
            switch (stage) {
                case JET:
                    renderQuad(0.4f * (1.5f - remainingLifespan / totalLifeSpan) * Math.min(acceleration.abs(), 1));
                case BALL:
                    renderBox(1f * (1 - remainingLifespan / totalLifeSpan) * Math.min(acceleration.abs(), 1));
                    break;
                case SMOG:
                    renderBox(0.75f * (1 - remainingLifespan / totalLifeSpan) * Math.min(acceleration.abs(), 1));
                    break;
                default:
                    renderQuad(0.5f * (1.5f - remainingLifespan / totalLifeSpan) * Math.min(acceleration.abs(), 1));
                    break;
            }
        } else {
            renderPoint();
        }
    }

    private Vec3 gradientColor() {
        return Vec3.of(255, 255 * (remainingLifespan / totalLifeSpan), 255 * Math.max(2 * remainingLifespan / totalLifeSpan - 1, 0));
    }

    private void changeStageToSmog() {
        // smog particles come out of jet and slow down due to high air resistance
        velocity = velocity.scale(parent.random(1));
        // their lifespan is increased to show their effects
        remainingLifespan += 70;
        totalLifeSpan += 70;
        stage = Stage.SMOG;
        if (parent.random(1) < 0.1) {
            shape = Shape.BOX;
        }
    }

    private void renderBox(float size) {
        parent.fill(color.x, color.y, color.z);
        parent.stroke(color.x, color.y, color.z);
        parent.pushMatrix();
        parent.translate(position.x, position.y, position.z);
        parent.box(size);
        parent.popMatrix();
    }

    private void renderQuad(float sideLen) {
        parent.fill(color.x, color.y, color.z);
        parent.stroke(color.x, color.y, color.z);
        parent.pushMatrix();

        parent.translate(position.x, position.y, position.z);
        parent.rotate(remainingLifespan / totalLifeSpan * parent.PI * 20, 0, 1, 0);
        parent.beginShape();
        parent.texture(texture);
        parent.vertex(-sideLen, -sideLen, 0, 0, 0);
        parent.vertex(sideLen, -sideLen, 0, texture.width, 0);
        parent.vertex(sideLen, sideLen, 0, texture.width, texture.height);
        parent.vertex(-sideLen, sideLen, 0, 0, texture.height);
        parent.endShape();

        parent.popMatrix();
    }

    private void renderPoint() {
        parent.stroke(color.x, color.y, color.z);
        parent.point(position.x, position.y, position.z);
    }
}
