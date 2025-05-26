# Java 21 Installation Instructions for Ubuntu

## Uninstall Java 17
```bash
sudo apt remove openjdk-17-jdk openjdk-17-jre
sudo apt autoremove
```

## Install Java 21
```bash
# Add the repository for AdoptOpenJDK
sudo apt update
sudo apt install -y wget apt-transport-https gnupg

# Import the Adoptium GPG key
wget -O - https://packages.adoptium.net/artifactory/api/gpg/key/public | sudo apt-key add -

# Add the Adoptium repository
echo "deb https://packages.adoptium.net/artifactory/deb $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/adoptium.list

# Update apt and install Java 21
sudo apt update
sudo apt install temurin-21-jdk

# Verify the installation
java -version
```

After installing Java 21, you can build your Maven project with:
```bash
mvn clean compile
```

