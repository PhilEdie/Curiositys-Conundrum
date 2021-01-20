class BombUFO extends UFO {

  float bombSize = 30;
  float bombSpeed = 10;
  float scoreValue = 100;

  BombUFO(int[] colorValues, float x, float y) {
    super(colorValues, x, y);
    this.UFOBody = allImages.get("BombUFO");
  }

  void update() {
    this.checkCollisions();
    this.display();
    this.checkOutOfBounds();
    this.dropBomb();
    position.x += speed;
  }


//Drops a bomb when above the rover. 
  void dropBomb() {
    if (this.ammo > 0) {
      if (this.position.x > main.rover.getX()
        && this.position.x < main.rover.getX() + main.rover.getWidth()) {
        main.activeBombs.add(new Bomb(this.position.x, this.position.y, this.bombSize, this.bombSpeed));
        this.ammo--;
      }
    }
  }
}
