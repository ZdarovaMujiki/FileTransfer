# FileTransfer
Application that transfer file using TCP and calculate data transfer speed

## Usage

### Build
./gradlew jar

### Run

#### Client
java -jar FileTransferTCPclient\build\libs\client.jar [filename] [server ip] [server port]

#### Server
java -jar FileTransferTCPserver\build\libs\server.jar [port]