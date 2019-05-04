module.exports = function (sequelize, DataTypes) {
  return sequelize.define(
    "event",
    {
      "type": {
        "type": DataTypes.STRING,
      },
      "punishment": {
        "type": DataTypes.STRING,
      },
      "userId": {
        "type": DataTypes.STRING,
      },
      "jobId": {
        "type": DataTypes.STRING,
      },
      "msg": {
        "type": DataTypes.STRING,
      }
    }
  );
};
