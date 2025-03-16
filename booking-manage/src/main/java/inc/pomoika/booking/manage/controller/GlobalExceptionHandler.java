package inc.pomoika.booking.manage.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;

import inc.pomoika.booking.common.exception.handler.CommonExceptionHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends CommonExceptionHandler {
} 