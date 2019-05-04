'use strict';
function rand(min,max) // min and max included
{
    return Math.floor(Math.random()*(max-min+1)+min);
}
module.exports = {
  up: (queryInterface, Sequelize) => {
      var policies = [
      ];
      for (var i = 0; i < 31; i++) {
        var policy = {
          appId: 34324,
          userId: 10000+i,
          type: "COUNT_POLICY",
          params: '{"numMaxEvents": 100, "eventTime": 30}',
          punishment: "UNSUBSCRIBE",
          createdAt: new Date(),
          updatedAt: new Date()
        }
        if (i % 7 === 0) {
          policy.type = "AGGREGATION_POLICY";
          policy.params = '{"aggregationTime": 30000}';
          policy.punishment = "KILL_JOB";
        } else if( i % 7 === 2) {
          policy.from = "12:00";
          policy.to = "14:00";
          policies.push({...policy, except: true, punishment: "MONETIZE"})
        }
        policies.push(policy);
      }
      policies.push(policy);
      return queryInterface.bulkInsert('policies', policies, {});
    
  },

  down: (queryInterface, Sequelize) => {
      return queryInterface.bulkDelete('policies', null, {});
  }
};
