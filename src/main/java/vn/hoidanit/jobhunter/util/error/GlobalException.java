package vn.hoidanit.jobhunter.util.error;

import java.util.List;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import vn.hoidanit.jobhunter.domain.response.RestResponse;

@RestControllerAdvice
public class GlobalException {
    // ko tồn tại tài khoản / mật khẩu trong DB
    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            BadCredentialsException.class,
            IdInvalidException.class
    })
    public ResponseEntity<RestResponse<Object>> handleIdException(Exception ex) {
        RestResponse<Object> restResponse = new RestResponse<Object>();
        restResponse.setMessage("Exception occurs....");
        restResponse.setError(ex.getMessage());
        restResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(restResponse);
    }

    @ExceptionHandler(value = {
            NoResourceFoundException.class,
            NullPointerException.class
    })
    public ResponseEntity<RestResponse<Object>> handleNotFoundException(Exception ex) {
        RestResponse<Object> restResponse = new RestResponse<Object>();
        restResponse.setMessage("404 not found. URL may not exist..");
        restResponse.setError(ex.getMessage());
        restResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
        return ResponseEntity.badRequest().body(restResponse);
    }

    // Tài khoản / mật khẩu trống
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> handleInvalidArgument(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();

        RestResponse<Object> restResponse = new RestResponse<Object>();
        restResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        restResponse.setError(ex.getBody().getDetail());

        List<String> errorsField = new ArrayList<String>();

        for (FieldError errors : fieldErrors) {
            errorsField.add(errors.getDefaultMessage());
        }

        restResponse.setMessage(errorsField.size() > 1 ? errorsField : errorsField.get(0));
        return ResponseEntity.badRequest().body(restResponse);
    }

    @ExceptionHandler(value = {
            StorageException.class
    })
    public ResponseEntity<RestResponse<Object>> handleUploadException(Exception ex) {
        RestResponse<Object> restResponse = new RestResponse<Object>();
        restResponse.setMessage("Exception upload file....");
        restResponse.setError(ex.getMessage());
        restResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(restResponse);
    }
}
