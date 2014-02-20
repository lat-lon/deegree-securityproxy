INSERT INTO usertable(access_token, user_name, password, layer_service_type_name, subscription_start, subscription_end ) VALUES ('VALID_HEADER','USER','PASSWORD', 'WCS', '2013-05-05', '2113-05-05');
INSERT INTO usertable(access_token, user_name, password, layer_service_type_name, subscription_start, subscription_end ) VALUES ('WMS_VALID_HEADER','WMSUSER','PASSWORD', 'WMS', '2013-05-05', '2113-05-05');

INSERT INTO usertable(access_token, user_name, password, serviceVersion, serviceName, operationType, layer_service_type_name, subscription_start, subscription_end ) 
 VALUES ('VALID_HEADER_GETCAPABILITIES','USER','PASSWORD', '<= 1.0.0', 'serviceName', 'GetCapabilities', 'WCS', '2013-05-05', '2113-05-05');
INSERT INTO usertable(access_token, user_name, password, serviceVersion, layerName, serviceName,  operationType, layer_service_type_name, subscription_start, subscription_end ) 
 VALUES ('VALID_HEADER_GETCOVERAGE','USER','PASSWORD', '<= 1.0.0', 'layerName', 'serviceName', 'GetCoverage', 'WCS', '2013-05-05', '2113-05-05');
INSERT INTO usertable(access_token, user_name, password, serviceVersion, serviceName, operationType, layer_service_type_name, subscription_start, subscription_end ) 
 VALUES ('VALID_HEADER_MULTIPLE','USER','PASSWORD', '<= 1.0.0', 'serviceName', 'GetCapabilities', 'WCS', '2013-05-05', '2113-05-05');
INSERT INTO usertable(access_token, user_name, password, serviceVersion, layerName, serviceName,  operationType, layer_service_type_name, subscription_start, subscription_end ) 
 VALUES ('VALID_HEADER_MULTIPLE','USER','PASSWORD', '<= 1.0.0', 'layerName', 'serviceName', 'GetCoverage', 'WCS', '2013-05-05', '2113-05-05');
INSERT INTO usertable(access_token, user_name, password, serviceVersion, layerName, serviceName,  operationType, layer_service_type_name, subscription_start, subscription_end ) 
 VALUES ('VALID_HEADER_MULTIPLE_VERSIONS','USER','PASSWORD', '<= 2.0.0', 'layerName', 'serviceName', 'GetCoverage', 'WCS', '2013-05-05', '2113-05-05');
 
INSERT INTO usertable(access_token, user_name, password, layer_service_type_name, subscription_start, subscription_end ) 
  VALUES ('VALID_HEADER_SUBSCRIPTION_OK','USER','PASSWORD', 'WCS', '2013-05-05', '2113-05-05');
INSERT INTO usertable(access_token, user_name, password, layer_service_type_name, subscription_start, subscription_end ) 
  VALUES ('VALID_HEADER_SUBSCRIPTION_EXPIRED','USER','PASSWORD', 'WCS', '2013-05-05', '2013-09-05');

INSERT INTO usertable(access_token, user_name, password, layer_service_type_name, subscription_start, subscription_end ) 
  VALUES ('VALID_HEADER_WITH_NULL_GEOMETRY_LIMIT','USER','PASSWORD', 'WCS', '2013-05-05', '2113-05-05');
INSERT INTO usertable(access_token, user_name, password, serviceName, layer_service_type_name, layerName, subscription_start, subscription_end, user_layer_limited_to ) 
  VALUES ('VALID_HEADER_WITH_GEOMETRY_LIMIT_ONE_RECORD','USER','PASSWORD', 'serviceName', 'WCS', 'layer1', '2013-05-05', '2113-05-05', 'SRID=4326;MULTIPOLYGON(((-89.739 20.864,-89.758 20.876,-89.765 20.894,-89.748 20.897,-89.73 20.91,-89.708 20.928,-89.704 20.948,-89.716 20.964,-89.729 20.99,-89.73 21.017,-89.712 21.021,-89.685 21.031,-89.667 21.025,-89.641 21.017,-89.62 21.019,-89.599 21.018,-89.575 20.995,-89.568 20.97,-89.562 20.934,-89.562 20.91,-89.577 20.89,-89.609 20.878,-89.636 20.877,-89.664 20.881,-89.683 20.904,-89.683 20.917,-89.664 20.941,-89.662 20.954,-89.674 20.965,-89.687 20.983,-89.705 20.989,-89.703 20.974,-89.696 20.961,-89.686 20.949,-89.683 20.935,-89.694 20.919,-89.705 20.901,-89.722 20.875,-89.727 20.869,-89.739 20.864),(-89.627 20.985,-89.603 20.962,-89.62 20.936,-89.634 20.943,-89.639 20.961,-89.649 20.975,-89.627 20.985)))');
INSERT INTO usertable(access_token, user_name, password, serviceName, layer_service_type_name, layerName, subscription_start, subscription_end, user_layer_limited_to ) 
  VALUES ('VALID_HEADER_WITH_GEOMETRY_LIMIT_TWO_RECORDS','USER','PASSWORD', 'serviceName', 'WCS', 'layer1', '2013-05-05', '2113-05-05', 'SRID=4326;MULTIPOLYGON(((-89.739 20.864,-89.758 20.876,-89.765 20.894,-89.748 20.897,-89.73 20.91,-89.708 20.928,-89.704 20.948,-89.716 20.964,-89.729 20.99,-89.73 21.017,-89.712 21.021,-89.685 21.031,-89.667 21.025,-89.641 21.017,-89.62 21.019,-89.599 21.018,-89.575 20.995,-89.568 20.97,-89.562 20.934,-89.562 20.91,-89.577 20.89,-89.609 20.878,-89.636 20.877,-89.664 20.881,-89.683 20.904,-89.683 20.917,-89.664 20.941,-89.662 20.954,-89.674 20.965,-89.687 20.983,-89.705 20.989,-89.703 20.974,-89.696 20.961,-89.686 20.949,-89.683 20.935,-89.694 20.919,-89.705 20.901,-89.722 20.875,-89.727 20.869,-89.739 20.864),(-89.627 20.985,-89.603 20.962,-89.62 20.936,-89.634 20.943,-89.639 20.961,-89.649 20.975,-89.627 20.985)))');
INSERT INTO usertable(access_token, user_name, password, serviceName, layer_service_type_name, layerName, subscription_start, subscription_end, user_layer_limited_to ) 
  VALUES ('VALID_HEADER_WITH_GEOMETRY_LIMIT_TWO_RECORDS','USER','PASSWORD', 'serviceName2', 'WCS', 'layer2', '2013-05-05', '2113-05-05', 'POLYGON');

INSERT INTO usertable(access_token, user_name, password, serviceVersion, serviceName, operationType, layer_service_type_name, subscription_start, subscription_end, internalServiceUrl )
  VALUES ('VALID_HEADER_INTERNAL_SERVICE_URL','USER','PASSWORD', '<= 1.0.0', 'serviceName', 'GetCapabilities', 'WCS', '2013-05-05', '2113-05-05', 'serviceUrl');
  
INSERT INTO usertable(access_token, user_name, password, serviceVersion, serviceName, operationType, layer_service_type_name, subscription_start, subscription_end, internalServiceUrl, requestParam1, requestParam2 )
  VALUES ('VALID_HEADER_WITH_REQUEST_PARAMS','USER','PASSWORD', '<= 1.0.0', 'serviceName', 'GetCapabilities', 'WCS', '2013-05-05', '2113-05-05', 'serviceUrl', 'addParam1', 'addParam2');
INSERT INTO usertable(access_token, user_name, password, serviceVersion, serviceName, operationType, layer_service_type_name, subscription_start, subscription_end, internalServiceUrl, requestParam1, requestParam2 )
  VALUES ('VALID_HEADER_WITH_ONE_EMPTY_REQUEST_PARAM','USER','PASSWORD', '<= 1.0.0', 'serviceName', 'GetCapabilities', 'WCS', '2013-05-05', '2113-05-05', 'serviceUrl', 'addParam1', '');