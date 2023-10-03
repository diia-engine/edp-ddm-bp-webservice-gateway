/*
 * Copyright 2023 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.bpwebservice.controller;

import com.epam.digital.data.platform.bpwebservice.dto.StartBpDto;
import com.epam.digital.data.platform.bpwebservice.dto.StartBpResponse;
import com.epam.digital.data.platform.bpwebservice.dto.rest.StartBpRestRequest;
import com.epam.digital.data.platform.bpwebservice.service.StartBpService;
import com.epam.digital.data.platform.starter.errorhandling.dto.SystemErrorDto;
import com.epam.digital.data.platform.starter.security.annotation.PreAuthorizeAnySystemRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoint that is used for starting business-process
 */
@RestController
@RequestMapping("/api")
@PreAuthorizeAnySystemRole
@RequiredArgsConstructor
@Tag(description = "Business process web service gateway Rest API", name = "bp-webservice-gateway-api")
public class StartBpController {

  private final StartBpService service;

  /**
   * Method that is used for starting business process instance
   * <p>
   * Delegates an invocation to {@link StartBpService#startBp(StartBpDto)}
   *
   * @param startBpRestRequest received {@link StartBpRestRequest startBpRequest}
   * @return {@link StartBpResponse startBpResponse} from service
   */
  @PostMapping("/start-bp")
  @Operation(
      summary = "Start process instance",
      description =
          "### Endpoint purpose:\n This endpoint allows you to start a business process instance based on the provided _businessProcessDefinitionKey_ in request body.\n"
              + "### Business process start validation:\n This endpoint requires valid _businessProcessDefinitionKey_ and _startVariables_. If no business process definition found or required parameters are missing, then _422_ response code returned.",
      parameters = @Parameter(
          in = ParameterIn.HEADER,
          name = "X-Access-Token",
          description = "Token used for endpoint security",
          required = true,
          schema = @Schema(type = "string")
      ),
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StartBpRestRequest.class),
              examples = {
                  @ExampleObject(value = "{\n"
                      + "  \"businessProcessDefinitionKey\": \"my-business-process\",\n"
                      + "  \"startVariables\": {\n"
                      + "    \"variable1\": \"value1\",\n"
                      + "    \"variable2\": \"value2\",\n"
                      + "    \"variable3\": null\n"
                      + "  }\n"
                      + "}"
                  )
              }
          )
      ),
      responses = {
          @ApiResponse(
              description = "Returns result variable of business process",
              responseCode = "200",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = StartBpResponse.class),
                  examples = {
                      @ExampleObject(value = "{\n"
                          + "  \"resultVariables\": {\n"
                          + "    \"return_var_1\": \"return_value_1\",\n"
                          + "    \"return_var_2\": null\n"
                          + "  }\n"
                          + "}"
                      )
                  })),
          @ApiResponse(
              description =
                  "Business process definition not found in trembita.process_definitions",
              responseCode = "404",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = SystemErrorDto.class))),
          @ApiResponse(
              description =
                  "Business process definition cannot be started or missing required start "
                      + "variable for the business process",
              responseCode = "422",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = SystemErrorDto.class))),
          @ApiResponse(
              description = "Internal server error",
              responseCode = "500",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = SystemErrorDto.class)))
      })
  @ResponseBody
  public StartBpResponse startBp(@RequestBody StartBpRestRequest startBpRestRequest) {
    var startBpDto = StartBpDto.builder()
        .businessProcessDefinitionKey(startBpRestRequest.getBusinessProcessDefinitionKey())
        .startVariables(startBpRestRequest.getStartVariables())
        .build();
    return service.startBp(startBpDto);
  }
}