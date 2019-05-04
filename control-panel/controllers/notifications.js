const notifications = (socket) => {
 
  socket.on('connect', obj => {
     console.log("connected")
  });
 
  socket.on('disconnect', () => {

  });
};

module.exports = notifications;