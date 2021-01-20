class Main {

  //https://www.youtube.com/watch?v=nermdWNIhm0 help with key use
  boolean[] keys = new boolean[128];

  GUI GUI;
  Rover rover;
  Score score;

  //Variables to hold color palette. 

  int color0[] = {30, 20, 70}; 
  int color1[] = {106, 44, 112}; 
  int color2[] = {184, 59, 94}; 
  int color3[] = {220, 118, 73};  
  int color4[] = {238, 236, 218};



  //Lists of all active Objects

  ArrayList<Background> backgroundLayers = new ArrayList<Background>();
  ArrayList<Upgrade> activeUpgrades = new ArrayList<Upgrade>();
  ArrayList<Explosion> activeExplosions = new ArrayList<Explosion>();
  ArrayList<Bullet> activeFriendlyBullets = new ArrayList<Bullet>();
  ArrayList<Bullet> activeEnemyBullets = new ArrayList<Bullet>();
  ArrayList<UFO> activeUFOs = new ArrayList<UFO>(); 
  ArrayList<Bomb> activeBombs = new ArrayList<Bomb>();
  ArrayList<Laser> activeLasers = new ArrayList<Laser>();


  //Temporary lists for dead objects. activeObjects use .removeAll() on deadObjects. 
  Mothership mothership;
  ArrayList<Laser> deadLasers = new ArrayList<Laser>();
  ArrayList<Bomb> deadBombs = new ArrayList<Bomb>();
  ArrayList<UFO> deadUFOs = new ArrayList<UFO>();
  ArrayList<Bullet> deadEnemyBullets = new ArrayList<Bullet>();
  ArrayList<Bullet> deadFriendlyBullets = new ArrayList<Bullet>();
  ArrayList<Explosion> deadExplosions = new ArrayList<Explosion>();
  ArrayList<Upgrade> deadUpgrades = new ArrayList<Upgrade>();

  //Keeps track of waves and total ufos. 
  int waveNum = 1;
  int UFOsInWave = 5;
  int UFOsLeft = UFOsInWave;


  //Probabilities for upgrades and UFOs
  float probUpgrade = 10;
  float probUFO = 1;
  float increaseProbUFO = 0.02;


  //Constant for the height of the ground
  float groundY = height/2 + height/3;


  boolean active = true;
  boolean lostGame = false;
  int livesLeft = 3;
  float bossWave = 5;  //Every 5 waves a boss will spawn. 


//The main class keeps track of all of the games objects. creating a new main object restarts the game. 

  Main() {
    this.GUI = new GUI(this.livesLeft, this.color4);
    GUI.setWaveNum(this.waveNum);
    rover = new Rover(this.color4, width/2, this.groundY + 20);
    backgroundLayers.add(new Background(this.color1, height/4, 0.5, 0));
    backgroundLayers.add(new Background(this.color2, height/3, 1, 0));
    backgroundLayers.add(new Background(this.color3, this.groundY, 1, 50));
    this.score = new Score();
  }


  void update() {
    clear();
    background(this.color0[0], this.color0[1], this.color0[2]);
    if (this.UFOsLeft <= 0) { 
      this.startNextWave();
    }
    this.removeDeadObjects();
    this.updateAllObjects();
    this.checkPressedKeys();
    this.GUI.update();
    if (this.waveNum % this.bossWave == 0) {
      this.startBossFight();
      return;
    }
    if (this.activeUFOs.size() < this.UFOsLeft) { 
      this.spawnUFO();
    }
  }


  //The player can use A OR Left arrow to move reft.
  //The player can use D OR Right arrow to move right. 
  void checkPressedKeys() {
    if (keys['a'] || keys['A'] || keys[LEFT]) { 
      rover.move(-1);
    }
    if (keys['d'] || keys['D'] || keys[RIGHT]) { 
      rover.move(1);
    }
  }

  //Clears all dead objects from the activeObjects lists.
  void removeDeadObjects() {
    this.activeUpgrades.removeAll(this.deadUpgrades);
    this.activeFriendlyBullets.removeAll(this.deadFriendlyBullets);
    this.activeEnemyBullets.removeAll(this.deadEnemyBullets);
    this.activeUFOs.removeAll(this.deadUFOs);
    this.activeBombs.removeAll(this.deadBombs);
    this.activeExplosions.removeAll(this.deadExplosions);
    this.activeLasers.removeAll(this.deadLasers);

    this.deadUpgrades.clear();
    this.deadFriendlyBullets.clear();
    this.deadEnemyBullets.clear();
    this.deadUFOs.clear();
    this.deadBombs.clear();
    this.deadExplosions.clear();
    this.deadLasers.clear();
  }

  //Increases wave number and the max UFOs per wave when called. 
  void startNextWave() {
    this.waveNum++;
    GUI.displayWaveNum();
    GUI.setWaveNum(this.waveNum); 
    this.UFOsLeft = this.waveNum * this.UFOsInWave;
  }


  void startBossFight() {
    if (this.mothership == null) {
      this.spawnMothership();
      this.UFOsLeft = 1;
    }
  }

  //Updates every object within the activeObjects lists. Also updates the GUI and score. 
  void updateAllObjects() {
    for (Background b : this.backgroundLayers) { 
      b.update();
    }
    for (Upgrade u : this.activeUpgrades) { 
      u.update();
    }
    if (this.active) {
      rover.update();
      for (Bullet b : this.activeFriendlyBullets) { 
        b.update();
      } 
      for (Bomb b : this.activeBombs) { 
        b.update();
      }
      for (UFO u : this.activeUFOs) { 
        u.update();
      }
      for (Explosion e : this.activeExplosions) { 
        e.updateParticles();
      }
      for (Bullet b : this.activeEnemyBullets) {
        b.update();
      }
    }
    this.score.update();
    this.GUI.update();
  }


  void spawnMothership() {
    this.mothership = new Mothership(this.color4, 0, 0);
    this.activeUFOs.add(this.mothership);
  }

  void spawnUFO() {
    float rnd = random(100);
    //Special UFOs will spawn after round 1. Probability of a special UFO is a fraction of overall UFO probability. 
    if (this.waveNum > 1 && rnd <= this.probUFO / 10) {
      this.spawnSpecialUFO();
      return;
    }

    //Spawns regular UFO.
    if (rnd <= this.probUFO) {
      this.activeUFOs.add(new BombUFO(this.color4, width, random(height/8, height/3)));

      this.probUFO += this.increaseProbUFO;
    }
  }

  //Called by Spawn UFO. A special UFO uses weapons other than bombs. Can be distinguished by a different color.  
  void spawnSpecialUFO() {
    float i = random(2);
    if (i > 1 && this.waveNum > 2) { 
      this.activeUFOs.add(new LaserUFO(this.color4, width, random(height/8, height/3)));
      return;
    }
    if (i > 0) { 
      this.activeUFOs.add(new GunnerUFO(this.color4, width, random(height/8, height/3)));
    }
  }


  void removeUFO(UFO u) { 
    this.deadUFOs.add(u);
    this.UFOsLeft--;
  }

  void removeEnemyBullet(Bullet b) {
    this.deadEnemyBullets.add(b);
  }

  void removeBomb(Bomb b) { 
    this.deadBombs.add(b);
  }

  void removeLaser(Laser l) {
    this.deadLasers.add(l);
  }

  void removeMothership(Mothership m) {
    this.deadUFOs.add(m);
    this.mothership = null;
    this.startNextWave();
  }

  Rover getRover() { 
    return this.rover;
  }

  Mothership getMothership() {
    if (this.mothership != null) {
      return this.mothership;
    }
    return null;
  }

  int getLives() { 
    return this.livesLeft;
  }

  int getWave() {
    return this.waveNum;
  }

  int getUFOsLeft() {
    return this.UFOsLeft;
  }

  //Sets active to false, which prevents activeObjects from being updated and displayed. 
  //Sets lostGame to true, which makes the GUI display the game over screen. 

  void loseGame() {
    this.active = false;
    this.lostGame = true;
  }
}
