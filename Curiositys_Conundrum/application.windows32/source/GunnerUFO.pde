class GunnerUFO extends UFO {
  int[] colorValues;
  float bulletSpeed = 10;
  float bulletAngle = PI/15;
  float gunLength = 50;


  GunnerUFO(int[] colorValues, float x, float y) {
    super(colorValues, x, y);
    this.colorValues = new int[] {0, 255, 255};
    this.UFOBody = allImages.get("GunnerUFO");
    this.scoreValue = 300;
  }

  void update() {
    this.checkCollisions();
    this.checkOutOfBounds();
    this.display();
    if (this.ammo > 0) {
      this.shoot();
    }
    position.x += speed;
  }

  void display() {
    tint(this.colorValues[0], this.colorValues[1], this.colorValues[2]);
    noStroke();
    image(this.UFOBody, this.position.x, this.position.y);
  }


//Shoots 3 bullets in a cone. Adds each bullet to an arraylist so that each bullet leaves the UFO at the same time. 
  void shoot() {
    
    if (this.position.x > main.rover.getX()
      && this.position.x < main.rover.getX() + main.rover.getWidth()) {

      ArrayList<Bullet> wave = new ArrayList<Bullet>();   
      PVector bulletPos = new PVector((this.position.x + this.w) - this.w/2, this.position.y + gunLength); 
      PVector speed = new PVector(0, this.bulletSpeed);

      //Bullet 1
      wave.add(new Bullet(this.colorValues, bulletPos, speed));        

      //Bullet 2
      bulletPos = new PVector((this.position.x + this.w)- this.w/2 + 10, this.position.y + gunLength); 
      speed = new PVector(sin(bulletAngle) * this.bulletSpeed, cos(bulletAngle) * this.bulletSpeed);
      wave.add(new Bullet(this.colorValues, bulletPos, speed)); 

      //Bullet 3
      bulletPos = new PVector((this.position.x + this.w)- this.w/2 - 10, this.position.y + gunLength); 
      speed = new PVector(sin(- bulletAngle) * this.bulletSpeed, cos(- bulletAngle) * this.bulletSpeed);
      wave.add(new Bullet(this.colorValues, bulletPos, speed));  

      main.activeEnemyBullets.addAll(wave);
      this.ammo--;
    }
  }
}
