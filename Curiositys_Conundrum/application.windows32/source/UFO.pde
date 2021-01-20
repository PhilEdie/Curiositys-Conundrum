class UFO {
  int[]colorValues;
  PImage UFOBody;
  PVector position = new PVector(0, 0);
  float w = 200;
  float h = 50;
  float speed = 10;
  int health = 1;
  int ammo = 1;
  int maxAmmo = 1;
  float bombSize = 30;
  float bombSpeed = 10;
  float scoreValue = 100;

  UFO(int[] colorValues, float x, float y) {
    this.UFOBody = allImages.get("BombUFO"); 
    this.colorValues = colorValues;
    this.position.x = x;
    this.position.y = y;
    int[] directions = {-1, 1};
    int direction = int(random(directions.length));
    this.speed = this.speed * directions[direction];
  }

  void update() {
    this.checkCollisions();
    this.display();
    this.checkOutOfBounds();
    position.x += speed;
  }
  
  
//Checks if the UFO is still on screen. Moves the UFO to the opposite side of the screen when out of bounds. 
  void checkOutOfBounds() {
    if (this.position.x + this.w < 0) {
      this.position.x = width;
      this.ammo = maxAmmo;
    }

    if (this.position.x > width) {
      this.position.x = 0 - this.w;
      this.ammo = maxAmmo;
    }
  }

  void display() {
    fill(this.colorValues[0], this.colorValues[1], this.colorValues[2]);
    tint(colorValues[0], colorValues[1], colorValues[2]);
    noStroke();
    image(this.UFOBody, this.position.x, this.position.y);
  }

//Checks to see if the UFO collides with any projectiles. 
  void checkCollisions() {
    for (Bullet b : main.activeFriendlyBullets) {
      if (b.collides(this.position.x, this.position.y, this.w, this.h)) {
        main.deadFriendlyBullets.add(b);
        this.loseHealth();
      }
    }
  }

  void loseHealth() {
    if (this.hasHealthLeft()) {
      this.health--;
    } else {
      this.destroyUFO();
    }
  }


//Removes the UFO from the game. Creates an explosion object where destroyed. Increases score.
//Has a chance to drop an upgrade. 

  void destroyUFO() {
    main.removeUFO(this);
    main.activeExplosions.add(new Explosion(this.colorValues, this.position, 10));
    main.score.increaseScore(this.scoreValue);
    
    float rnd = random(100);
    if (rnd <= main.probUpgrade) {
      main.activeUpgrades.add(new Upgrade(this.position.x, this.position.y));
    }
  }

  boolean hasHealthLeft() {
    return(this.health > 0);
  }

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
}
