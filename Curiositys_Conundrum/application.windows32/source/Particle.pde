class Particle {
  PVector position = new PVector(0, 0);
  PVector velocity = new PVector(0, 0);
  PVector acceleration = new PVector(0, 0);
  float lifespan;
  int[] colorValues;

//Particles are created by the explosion class when destroying basic UFOs or damaging the mothership. 
  Particle(int[] colorValues, PVector l) {
    this.colorValues = colorValues;
    this.acceleration = new PVector(0, 0.7);
    this.velocity = new PVector(random(-5, 5), random(-10, 5));
    this.position = l.copy();
    this.lifespan = 255.0;
  }

  void update() {
    this.velocity.add(this.acceleration);
    this.position.add(this.velocity);
    lifespan -= 1.0;
  }

  void display() {
    noStroke();
    fill(255, this.lifespan); 
    ellipse(position.x, position.y, 10, 10);
  }

  boolean isDead() {
    if (this.lifespan < 0) {
      return true;
    } else {
      return false;
    }
  }
}
