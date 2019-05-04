module.exports = function (sequelize, DataTypes) {
  return sequelize.define(
    "policy",
    {
      "userId": {
        "type": DataTypes.STRING,
      },
      "appId": {
        "type": DataTypes.STRING,
      },
      "type": {
        "type": DataTypes.STRING,
      },
      "params": {
        "type": DataTypes.JSON,
      },
      "punishment": {
        "type": DataTypes.STRING,
      },
      "from": {
        type: DataTypes.STRING,
      },
      "to": {
        type: DataTypes.STRING,
      },
      "except": {
        type: DataTypes.BOOLEAN,
        defaultValue: false
      },
    }
  );
};
