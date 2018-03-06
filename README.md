# Tutorial IoT
A. When a DeviceManager receives a request with a group and device id:
  * If the manager already has an actor for the device group, it forwards the request to it.
  * Otherwise, it creates a new device group actor and then forwards the request.
    
B. The DeviceGroup actor receives the request to register an actor for the given device:
  * If the group already has an actor for the device, the group actor forwards the request to the device actor.
  * Otherwise, the DeviceGroup actor first creates a device actor and then forwards the request.  
  
C. The device actor receives the request and sends an acknowledgement to the original sender. Since the device actor acknowledges receipt (instead of the group actor), the sensor will now have the ActorRef to send messages directly to its actor.