'use strict';

module.exports = {
  up: (queryInterface, Sequelize) => {
    return queryInterface.createTable('policies', { "id": {
      "type": Sequelize.INTEGER,
      "allowNull": false,
      "primaryKey": true,
      "autoIncrement": true,
      "unique": true
    },
    "userId": {
      "type": Sequelize.STRING,
    },
    "appId": {
      "type": Sequelize.STRING,
    },
    "type": {
      "type": Sequelize.STRING,
    },
    "params": {
      "type": Sequelize.JSON,
    },
    "punishment": {
      "type": Sequelize.STRING,
    },
    "from": {
      "type": Sequelize.STRING,
    },
    "to": {
      "type": Sequelize.STRING,
    },
    "except": {
      "type": Sequelize.BOOLEAN,
      "defaultValue": false
    },
    "createdAt": {"type": Sequelize.DATE,
    "allowNull": false},
    "updatedAt": {"type": Sequelize.DATE,
    "allowNull": false}
  },{"sync": {"force": true}});
  },

  down: (queryInterface) => {
    return queryInterface.dropTable('policies');
  }
};