class Bomb {
  PVector position = new PVector();
  float size = 60;
  float maxSize = 200;
  PVector speed = new PVector();
  float damage;
  boolean exploding = false;
  boolean hitMaxSize = false;
  float scoreValue = 50;
  boolean alreadyExploded = false;



  Bomb(float x, float y, float size, float speed) {
    this.size = size;
    this.position.x = x;
    this.position.y = y;
    this.speed.y = speed;
  }

  void update() {
    this.moveBomb();
    this.checkBulletCollisions();
    if (this.exploding) {
      this.checkBombCollisions();
      this.growExplosion();
    }
    this.display();
  }


  void display() {
    noStroke();
    fill(255);
    ellipse(this.position.x, this.position.y, this.size, this.size);
  }


//Increases the y when not exploding. Starts explosion when hitting the ground. 
  void moveBomb() {
    if (!this.exploding) {
      this.position.y += this.speed.y;
    }
    if (this.hitTheGround()) {
      this.startExplosion();
    }
  }

//Sets exploding and alreadyExploded to true. Plays an explosion sound. 
  void startExplosion() {
    this.exploding = true;
    if (!this.alreadyExploded) { 
     // allSounds.get("Bomb").play();
      this.alreadyExploded = true;
    }
  }

//Increases the radius of the bomb. Deletes bomb when it gets too big. 
  void growExplosion() {
    this.size +=20;
    if (this.size > this.maxSize) {
      main.removeBomb(this);
    }
  }



//Starts the explosion if hit by a bullet. Increases score. 
  void checkBulletCollisions() {
    for (Bullet b : main.activeFriendlyBullets) {
      if (!this.alreadyExploded && this.collides(b.getX(), b.getY(), b.getSize(), b.getSize())) {
        main.score.increaseScore(this.scoreValue);
        this.startExplosion();
      }
    }
  }
  
  
  //Called while exploding. Makes other bombs explode if they collide with the blast radius. Increases score. 
  void checkBombCollisions() {
    for (Bomb b : main.activeBombs) {
      if (!b.alreadyExploded && !this.equals(b) && this.collides(b.getX(), b.getY(), b.getSize(), b.getSize())) {
        main.score.increaseScore(this.scoreValue);
        b.startExplosion();
      }
    }
  }


  float getX() {
    return this.position.x;
  }

  float getY() {
    return this.position.y;
  }

  float getSize() {
    return this.size;
  }

  boolean hitTheGround() {
    return (this.position.y > height/2 + height/3);
  }

  boolean collides(float otherX, float otherY, float otherW, float otherH) {
    if (this.position.x + this.size > otherX
      && this.position.x < otherX + otherW
      && this.position.y + this.size > otherY
      && this.position.y < otherY + otherH) {
      return true;
    }  
    return false;
  }
}
