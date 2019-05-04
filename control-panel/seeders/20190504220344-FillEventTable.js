'use strict';
function rand(min,max) // min and max included
{
    return Math.floor(Math.random()*(max-min+1)+min);
}
module.exports = {
  up: (queryInterface, Sequelize) => {
      var events = [];
      for (var i = 0; i < 51; i++) {
        var date = new Date();
        date.setDate(date.getDate() - Math.round(i/3));

        var event = {
            id: i+1,
            userId: rand(10000,10030),
            jobId: rand(5545545,5545585),
            createdAt: date,
            updatedAt: date
        };
        if (i % 7) {
           event = { ...event,
            type: "COUNT_POLICY",
            msg: "You have received "+ rand(200,300)  +" events in 30 seconds, which is over the established threshold of 40",
            punishment: !(i % 9) ? "MONETIZE" : "UNSUBSCRIBE"
          };
        } else {
           event = { ...event,
            type: "AGGREGATION_POLICY",
            msg: "You have to aggregate the received data before saving",
            punishment: "KILL_JOB"
          }
        }
        events.push(event);
      }
      return queryInterface.bulkInsert('events', events, {});
    
  },

  down: (queryInterface, Sequelize) => {
      return queryInterface.bulkDelete('events', null, {});
  }
};
