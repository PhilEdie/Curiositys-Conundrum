class Rover {
  int[] colorValues;
  PImage roverBody;
  PVector position = new PVector(0, 0);
  PVector cannonBase = new PVector(0, 0);
  PVector cannonEnd = new PVector(0, 0);
  float w = 150;
  float h = 50;
  float cannonLength = 100;
  float ouchOpacity = 0;

  //Stats
  float speed = 10;
  float bulletSpeed = 15;
  float fireRate = 10;

  boolean firing = false;
  float firingCounter = 0; 

  Rover(int[] colorValues, float x, float y) {
    this.colorValues = colorValues;
    this.position = new PVector(x, y);
    this.position.x = x;
    this.position.y = y;
    this.cannonBase.x = x + 20;
    this.cannonBase.y = y - 20;
    this.roverBody = allImages.get("Rover");
  }

  void update() {
    this.checkCollisions();
    this.drawRover();
    this.drawCannon();
    if (this.firing && this.canShoot()) {  
      this.shoot();
    }
    if(this.ouchOpacity > 0){
      this.drawOuch();
    }
  }
  
  //Displays test saying "Ouch!" above the rover after taking damage".
  void drawOuch(){
      fill(255, 255, 255, this.ouchOpacity);
      this.ouchOpacity = this.ouchOpacity - 2;
      textSize(50);
      textAlign(CENTER, CENTER);
      text("OUCH!", this.position.x + (this.w / 2), this.position.y - 80);
    
  }

  void drawRover() {
    noStroke();
    fill(colorValues[0], colorValues[1], colorValues[2]);
    tint(colorValues[0], colorValues[1], colorValues[2]);
    image(roverBody, this.position.x - 20, this.position.y - 50);
  }


//Draws a line to represent the cannon. The line is directed at the mouse. 
  void drawCannon() {
    //With help from https://wiki.processing.org/examples/vectormath.html
    pushMatrix();
    PVector mouse = new PVector(mouseX, mouseY);
    this.cannonEnd = new PVector(cannonBase.x, cannonBase.y);
    mouse.sub(this.cannonBase);
    mouse.normalize();
    mouse.mult(this.cannonLength);
    translate(cannonBase.x, cannonBase.y);
    stroke(colorValues[0], colorValues[1], colorValues[2]);
    strokeWeight(15);
    line(0, 0, mouse.x, mouse.y);
    popMatrix();
  }



//Shoots a bullet from the end of the cannon in the direction of the mouse. 
  void shoot() {
    //With help from http://studio.processingtogether.com/sp/pad/export/ro.91kLmk61vAOZp/latest
    PVector bulletVector = new PVector(mouseX, mouseY);
    pushMatrix();
    //Help from tutor
    PVector newVector = cannonBase.copy().add(bulletVector.copy().sub(cannonBase).normalize().mult(this.cannonLength));
    bulletVector.sub(this.cannonBase);
    bulletVector.normalize();
    bulletVector.mult(this.bulletSpeed);
    Bullet b = new Bullet(this.colorValues, newVector, bulletVector);
    main.activeFriendlyBullets.add(b);
    popMatrix();
  }


//Controls the fire rate.
  boolean canShoot() {
    if (this.firingCounter >= this.fireRate) {
      this.firingCounter = 0;
      return true;
    } else {
      this.firingCounter++;
      return false;
    }
  }


  void move(int direction) {
    this.position.x += direction * this.speed;
    this.cannonBase.x += direction * this.speed;
    this.checkOutOfBounds();
  }


//Ensures rover can't drive off screen. 
  void checkOutOfBounds() {
    if (this.position.x < 0) {
      this.position.x += this.speed;
      this.cannonBase.x += this.speed;
    }
    if (this.position.x + this.w > width) {
      this.position.x -= this.speed;
      this.cannonBase.x -= this.speed;
    }
  }

  boolean collides(float otherX, float otherY, float otherW, float otherH) {
    if (this.position.x + this.w > otherX
      && this.position.x < otherX + otherW
      && this.position.y + this.h > otherY
      && this.position.y < otherY + otherH) {
      return true;
    }  
    return false;
  }

//Checks to see if the rover collides with any active projectiles. 

  void checkCollisions() {
    if (main.activeBombs.size() > 0) {
      for (Bomb b : main.activeBombs) {
        if(this.collides(b.getX(), b.getY(), b.getSize(), b.getSize())){
          this.ouchOpacity = 255;
          main.GUI.healthBar.loseLife();
        }
        
      }
    }
    if (main.activeLasers.size() > 0) {
      for (Laser l : main.activeLasers) {
        if(this.collides(l.getX(), l.getY(), l.getWidth(), l.getHeight())){
          this.ouchOpacity = 255;
         main.GUI.healthBar.loseLife(); 
        }
      }
    }
    
    if(main.activeEnemyBullets.size() > 0){
      for(Bullet b : main.activeEnemyBullets){
        if(this.collides(b.getX(), b.getY(), b.getSize(), b.getSize())){
          main.GUI.healthBar.loseLife();
          this.ouchOpacity = 255;
          main.deadEnemyBullets.add(b);
        }
      }
    }
  }
  
  //Getter and Setter methods.

  float getX() { 
    return this.position.x;
  }
  float getY() { 
    return this.position.y;
  }
  float getWidth() { 
    return this.w;
  }
  float getHeight() { 
    return this.h;
  }

  float getSpeed() { 
    return this.speed;
  } 
  void setSpeed(float speed) { 
    this.speed = speed;
  }

  float getFireRate() { 
    return this.fireRate;
  }
  void setFireRate(float rate) { 
    this.fireRate = rate;
  }

  float getBulletSpeed() { 
    return this.bulletSpeed;
  }
  void setBulletSpeed(float speed) { 
    this.bulletSpeed = speed;
  }

  void increaseFireRate() {
    this.fireRate = this.fireRate * 0.9;
  }

  void increaseSpeed() {
    this.speed +=0.6;
  }

  void increaseBulletSpeed() {
    this.bulletSpeed +=1;
  }

}
