'use strict';
module.exports = {
  up: (queryInterface, Sequelize) => queryInterface.createTable('events', {
    id: {
      type: Sequelize.INTEGER,
      allowNull: false,
      primaryKey: true,
      autoIncrement: true,
      unique: true,
    },
    type: {
      type: Sequelize.STRING,
    },
    userId: {
      type: Sequelize.STRING,
    },
    jobId: {
      type: Sequelize.STRING,
    },
    msg: {
      type: Sequelize.STRING,
    },
    punishment: {
      type: Sequelize.STRING,
    },
    createdAt: {
      type: Sequelize.DATE,
      allowNull: false,
    },
    updatedAt: {
      type: Sequelize.DATE,
      allowNull: false,
    },
  }, { sync: { force: true } }),

  down: (queryInterface, Sequelize) => queryInterface.dropTable('events'),
};
