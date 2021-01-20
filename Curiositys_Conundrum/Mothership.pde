class Mothership extends UFO {
  float bombSize = 30;
  float bombSpeed = 10;
  float scoreValue = 10000;
  float baseHeight = 150;
  boolean completedIntro = false;
  float lastDroppedBomb = millis();
  float bombCooldown = 400;
  boolean dying = false;
  ArrayList<ExplodingMothership> explosions = new ArrayList<ExplodingMothership>();
  ArrayList<ExplodingMothership> deadExplosions = new ArrayList<ExplodingMothership>();
  float lastExploded = millis();
  float explosionCooldown = 100;
  MothershipHealthBar healthBar;

  Mothership(int[] colorValues, float x, float y) {
    super(colorValues, x, y);
    this.w = 1000;
    this.h = 200;
    this.health = 100;
    this.scoreValue = 10000;
    this.speed = 2;
    this.UFOBody = allImages.get("Mothership");
    this.completedIntro = false;
    this.startAtTop();
  }

  void startAtTop() {
    this.position = new PVector(width/2 - this.w/2, 0 - this.h);
  }

  //Increases the motherships y until it is at its base y. Once this happens, the mothership becomes active and can be interacted with. 
  void enterScreen() {
    if (this.position.y < this.baseHeight) {
      this.position.y += 2;
    } else { 
      this.completedIntro = true;
      this.healthBar = new MothershipHealthBar(this.position.x + this.w/2, this.position.y - 25, this.position.x + this.w, this.health);
    }
  }

  void update() {
    this.display();
    if (!this.completedIntro) {
      this.enterScreen();
      return;
    }
    if (this.dying) {
      this.playDyingAnimation();
      return;
    }
    this.healthBar.update();
    this.checkCollisions();
    this.checkOutOfBounds();
    this.dropBomb();
    this.position.x += this.speed;
    this.healthBar.move(this.speed);
  }


  //Bounces off edge of screen. 
  void checkOutOfBounds() {
    if (this.position.x < 0) {
      this.position.x = 0;
      this.speed = this.speed * -1;
    }
    if (this.position.x + this.w > width) {
      this.position.x  = width - this.w;
      this.speed = this.speed * -1;
    }
  }

  @Override
    void checkCollisions() {
    for (Bullet b : main.activeFriendlyBullets) {
      if (b.collides(this.position.x, this.position.y, this.w, this.h)) {
        main.deadFriendlyBullets.add(b);
        this.loseHealth();
        main.activeExplosions.add(new Explosion(this.colorValues, b.position, 3));
      }
    }
  }

  //Drops a bomb when above the rover. 
  void dropBomb() {
    if (millis() - this.lastDroppedBomb > this.bombCooldown) {
      if (this.position.x < main.rover.getX()
        && this.position.x + this.w > main.rover.getX() + main.rover.getWidth()) {
        main.activeBombs.add(new Bomb(random(this.position.x, this.position.x + this.w), this.position.y + this.bombSize, this.bombSize, this.bombSpeed));
        this.lastDroppedBomb = millis();
      }
    }
  }


  @Override
    void loseHealth() {
    if (this.hasHealthLeft()) {
      this.health--;
      this.healthBar.loseHealth();
    } else {
      this.destroyUFO();
    }
  }


  //Drops 3 active upgrades instead of 1. Has a dying animation which is turned on when destroyUFO() is called. 
  @Override
    void destroyUFO() {
    if (!this.dying) { 
      for (int i = 0; i < 3; i++) {
        main.activeUpgrades.add(new Upgrade(random(this.position.x, this.position.x + this.w), random(this.position.y, this.position.y + this.h)));
      }
      main.score.increaseScore(this.scoreValue);
      this.dying = true;
    }
  }


  //Animates explosions around the Mothership as the mothership leaves the screen. Once the mothership exits the screen, it is deleted. 
  void playDyingAnimation() {
    this.explosions.removeAll(this.deadExplosions);
    this.position.y -= 2;
    if (millis() - this.lastExploded > this.explosionCooldown) {
      this.explosions.add(new ExplodingMothership(random(this.position.x, this.position.x + this.w), random(this.position.y, this.position.y + this.h)));
      this.lastExploded = millis();
    }
    for (ExplodingMothership e : this.explosions) {
      e.update();
    }
    if (this.position.y + this.h + 100 < 0) {
      main.removeMothership(this);
    }
  }

  //Clears all completed explosions. 

  void removeExplosion(ExplodingMothership e) {
    this.deadExplosions.add(e);
  }
}
