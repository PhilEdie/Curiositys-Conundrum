class Upgrade {
  PVector position;
  float velocity = 5;
  String[] possibleUpgrades = {"Speed", "Fire Rate", "Bullet Speed"};
  String name;
  float size = 50;

  Upgrade(float x, float y) {
    this.position = new PVector(x, y);
    //Randomly selects an upgrade from the array of possible upgrades. 
    this.name = possibleUpgrades[(int) random(0, possibleUpgrades.length)];    
  }
//Checks if the upgrade collides with the rover. Otherwise, moves the upgrade and displays it. 
  void update() {
    if (main.getRover().collides(this.position.x, this.position.y, this.size, this.size)) {
      main.deadUpgrades.add(this);
      this.applyUpgrade();
    }
    this.move();
    this.display();
  }

  void move() {
    if (this.position.y < main.groundY + 50) {
      this.position.y += velocity;
    }
  }

  void display() {
    fill(0, 255, 0);
    ellipseMode(CENTER);
    ellipse(this.position.x, this.position.y, this.size, this.size);
  }


//Upgrades the Rover depending on the upgrades name assigned in the constructor. 
  void applyUpgrade() {
    if (this.name.equals("Speed")) { 
      main.getRover().increaseSpeed();
      main.GUI.setUpgrade("Speed");
    } else if (this.name.equals("Fire Rate")) {
      main.getRover().increaseFireRate();
     main.GUI.setUpgrade("Fire Rate");
    } else if (this.name.equals("Bullet Speed")) {
      main.getRover().increaseBulletSpeed();
      main.GUI.setUpgrade("Bullet Speed");
    }
  }
}
