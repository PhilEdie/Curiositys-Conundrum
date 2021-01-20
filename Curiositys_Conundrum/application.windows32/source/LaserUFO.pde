class LaserUFO extends UFO {

  float aimRange = 500;
  boolean charging = false;
  float currentChargeTime = 0;
  float maxChargeTime = 50;

  boolean firing = false;
  float currentFiringTime = 0;
  float maxFiringTime = 20;

  float beamX;
  float beamY;
  float beamWidth = 5;
  Laser activeLaser;

  LaserUFO(int[] colorValues, float x, float y) {
    super(colorValues, x, y);
    this.UFOBody = allImages.get("LaserUFO"); 
    this.beamX = this.position.x + 100;
    this.beamY = this.position.y + this.h;
    this.scoreValue = 500;
    this.ammo = 1;
  }

  void update() {
    this.checkOutOfBounds();
    this.checkCollisions();
    this.display();
    this.updateLaser();
    if (this.ammo > 0) { 
      this.attemptToShoot();
    }
    if (this.charging) { 
      this.chargeLaser();
    }
    position.x += speed;
    this.beamX = this.position.x + 100;
    this.beamY = this.position.y + this.h;
  }

  void display() {
    tint(255, 0, 255);
    fill(this.colorValues[0], this.colorValues[1], this.colorValues[2]);
    noStroke();
    image(this.UFOBody, this.position.x, this.position.y);
  }

//Updates the laser. Removes the laser if the laser has completed its animation. 
  void updateLaser() {
    if (this.activeLaser == null) { 
      return;
    }
    this.activeLaser.update(this.speed);
    if (this.activeLaser.isComplete()) {
      main.removeLaser(this.activeLaser);
      this.activeLaser = null;
    }
  }

//Begins to charge laser when in range of Rover. 
  void attemptToShoot() {
    if (this.aboveRover()) {
      this.currentChargeTime = 0;
      this.charging = true;  
      this.ammo--;
    }
  }

//Displays red aiming beam, waits until it reaches maxChargeTime before firing laser. 
  void chargeLaser() {
    fill(255, 0, 0);
    rectMode(CORNER);
    rect(this.beamX - 1, this.beamY + 6, 2, height);
    this.currentChargeTime++;
    if (this.currentChargeTime >= this.maxChargeTime) {
      this.currentFiringTime = 0;
      this.activeLaser = new Laser(this.beamX, this.beamY, this.beamWidth, this.maxFiringTime);
      main.activeLasers.add(this.activeLaser);
      this.charging = false;
    }
  }


//Checks if it is near the rover. 
  boolean aboveRover() {
    return(this.position.x > main.rover.getX() - this.aimRange
      && this.position.x < main.rover.getX() + main.rover.getWidth() + this.aimRange);
  }

//Removes the UFO and its laser when destroyed.
  void loseHealth() {
    if (this.hasHealthLeft()) {
      this.health--;
    } else {
      main.removeLaser(this.activeLaser);
      this.activeLaser = null;
      this.destroyUFO();
    }
  }
}
