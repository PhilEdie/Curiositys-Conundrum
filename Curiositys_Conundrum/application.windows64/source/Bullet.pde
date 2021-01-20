class Bullet {
  int[] colorValues;
  PVector position = new PVector(0, 0);
  float size = 15;
  PVector speed = new PVector(0, 0);
  float damage;

  Bullet(int[] colorValues, PVector position, PVector speed) {
    this.colorValues = colorValues;
    this.position = position;
    this.speed = speed;
  }

  void update() {
    this.checkOutOfBounds();
    this.moveBullet();
    this.display();
  }

  void display() {
    fill(this.colorValues[0], this.colorValues[1], this.colorValues[2]);
    noStroke();
    ellipse(this.position.x, this.position.y, this.size, this.size);
  }

  void moveBullet() { 
    this.position.add(this.speed);
  } 


//Returns true if bullet is off screen. 
  boolean outOfBounds() {
    return(this.position.x > width || this.position.x < 0 
      || this.position.y > height || this.position.y < 0);
  }


//Removes the bullet if it leaves the screen. 
  void checkOutOfBounds() {
    if (this.outOfBounds()) { 
      main.deadFriendlyBullets.add(this);
      main.deadEnemyBullets.add(this);
    }
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
  
  
  float getX() { 
    return this.position.x;
  }

  float getY() { 
    return this.position.y;
  }

  float getSize() { 
    return this.size;
  }
}
