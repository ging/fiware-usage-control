const path = require("path");

// Load ORM
const Sequelize = require("sequelize");

const url = process.env.DATABASE_URL || "sqlite:db.sqlite";

const sequelize = new Sequelize(url);// Import the definition of the Escape Room Table from escapeRoom.js


// Session
sequelize.import(path.join(__dirname, "session"));

// Import the definition of the policy Table from policy.js
sequelize.import(path.join(__dirname, "policy"));

// Import the definition of the event Table from event.js
sequelize.import(path.join(__dirname, "event"));


// Relation between models
const {policy, event} = sequelize.models;// Relation 1-to-N between Escape Room and Turn:

/*event.belongsTo(policy);
policy.hasMany(event, {"onDelete": "CASCADE",
  "hooks": true});
*/


module.exports = sequelize;