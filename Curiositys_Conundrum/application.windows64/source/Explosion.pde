class Explosion {
  ArrayList<Particle> particles;
  ArrayList<Particle> deadParticles = new ArrayList<Particle>();
  PVector origin;
  int[] colorValues;
  int numParticles = 10;

  //Controls an array of particles which move independently. Called when destroying a basic UFO or shooting the Mothership.
  Explosion(int[] colorValues, PVector position, int numParticles) {
    this.colorValues = colorValues;
    this.origin = position.copy();
    this.numParticles = numParticles;
    this.particles = new ArrayList<Particle>();
    for (int i = 0; i < this.numParticles; i++) { 
      this.addParticle();
    }
  }

  void addParticle() { 
    this.particles.add(new Particle(this.colorValues, this.origin));
  }

  //Removes the explosion object if all particles are dead. Otherwise, updates the remaining particles. 
  void updateParticles() {
    if (completedExplosion()) {
      main.deadExplosions.add(this);
      return;
    }
    for (Particle p : this.particles) {     
      p.update();    
      p.display();
      if (p.isDead()) {
        this.deadParticles.add(p);
      }
    }
    this.particles.removeAll(this.deadParticles);
  }


  //Checks to see if any particles are still active. A particle is no longer active when it is off screen. 
  boolean completedExplosion() {
    for (Particle p : this.particles) {
      if (!p.isDead()) { 
        return false;
      }
    }
    return true;
  }
}
