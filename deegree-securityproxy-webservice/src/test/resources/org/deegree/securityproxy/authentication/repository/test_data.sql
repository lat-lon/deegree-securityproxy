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
 VALUES ('VALID_HEADER_MULTIPLE_VERSIONS','USER','PASSWORD', '<= 1.3.0', 'layerName', 'serviceName', 'GetCoverage', 'WCS', '2013-05-05', '2113-05-05');
 
INSERT INTO usertable(access_token, user_name, password, layer_service_type_name, subscription_start, subscription_end ) 
  VALUES ('VALID_HEADER_SUBSCRIPTION_OK','USER','PASSWORD', 'WCS', '2013-05-05', '2113-05-05');
INSERT INTO usertable(access_token, user_name, password, layer_service_type_name, subscription_start, subscription_end ) 
  VALUES ('VALID_HEADER_SUBSCRIPTION_EXPIRED','USER','PASSWORD', 'WCS', '2013-05-05', '2013-09-05');