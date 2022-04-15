package com.imooc.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.ModelAndView;



@ControllerAdvice
public class SellerAuthorizeException extends RuntimeException {
}
