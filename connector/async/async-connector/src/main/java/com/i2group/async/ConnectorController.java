/********************************************************************************
# * Licensed Materials - Property of IBM
# * (C) Copyright IBM Corporation 2021. All Rights Reserved
# *
# * This program and the accompanying materials are made available under the
# * terms of the Eclipse Public License 2.0 which is available at
# * http://www.eclipse.org/legal/epl-2.0.
# *
# * US Government Users Restricted Rights - Use, duplication or
# * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
# *
# ********************************************************************************/

package com.i2group.async;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.util.MimeTypeUtils.APPLICATION_XML_VALUE;

import javax.validation.Valid;

import com.i2group.async.rest.transport.AsyncQueryResponse;
import com.i2group.async.rest.transport.AsyncStatusResponse;
import com.i2group.async.rest.transport.ConnectorResponse;
import com.i2group.async.rest.transport.request.ConnectorRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/** Defines endpoints used by i2 Analyze to acquire data */
@RestController
public class ConnectorController {

  private final ExternalConnectorDataService connectorDataService;
  @Value("classpath:config.json")
  private Resource configResource;
  @Value("classpath:async-schema.xml")
  private Resource schemaResource;
  @Value("classpath:async-charting-schemes.xml")
  private Resource chartingSchemesResource;

  /**
   * Creates an instance of the ExternalConnectorDataService used to complete
   * acquires.
   *
   * @param connectorDataService The class containing operations which perform the
   *                             operation defined by the various services.
   */
  public ConnectorController(ExternalConnectorDataService connectorDataService) {
    this.connectorDataService = connectorDataService;
  }

  /**
   * Defines the /config endpoint which acquires the connector configuration data.
   *
   * @return The config.json file.
   */
  @RequestMapping(method = RequestMethod.GET, value = "/config", produces = APPLICATION_JSON_VALUE)
  public Resource config() {
    return configResource;
  }

  /**
   * Defines the endpoint used to retrieve the connector schema.
   *
   * @return The connector schema.
   */
  @RequestMapping(method = RequestMethod.GET, value = "/schema", produces = APPLICATION_XML_VALUE)
  public Resource schema() {
    return schemaResource;
  }

  /**
   * Defines the endpoint used to retrieve the connector charting schemes.
   *
   * @return The connector charting schemes.
   */
  @RequestMapping(method = RequestMethod.GET, value = "/charting-schemes", produces = APPLICATION_XML_VALUE)
  public Resource chartingSchemes() {
    return chartingSchemesResource;
  }

  /**
   * Defines the /async endpoint.
   *
   * @return The query response.
   */
  @RequestMapping(method = RequestMethod.POST, value = "/async", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public AsyncQueryResponse asyncAcquireService(@Valid @RequestBody ConnectorRequest request) {
    return connectorDataService.asyncAcquire(request.payload.conditions);
  }

  /**
   * Defines the /async/{queryId} endpoint.
   *
   * @return The status response.
   */
  @RequestMapping(method = RequestMethod.GET, value = "/async/{queryId}", produces = APPLICATION_JSON_VALUE)
  public AsyncStatusResponse asyncStatusService(@Valid @PathVariable("queryId") String queryId) {
    return connectorDataService.asyncStatus(queryId);
  }

  /**
   * Defines the /async/{queryId}/results endpoint.
   *
   * @return The results of the query.
   */
  @RequestMapping(method = RequestMethod.GET, value = "/async/{queryId}/results", produces = APPLICATION_JSON_VALUE)
  public ConnectorResponse asyncResultsService(@Valid @PathVariable("queryId") String queryId) {
    return connectorDataService.asyncResults(queryId);
  }

  /**
   * Defines the /async/{queryId} endpoint.
   *
   * @return The response with the removed query.
   */
  @RequestMapping(method = RequestMethod.DELETE, value = "/async/{queryId}", produces = APPLICATION_JSON_VALUE)
  public @ResponseBody ConnectorResponse asyncDeleteService(@Valid @PathVariable("queryId") String queryId) {
    return connectorDataService.asyncDelete(queryId);
  }
}
