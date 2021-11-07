package com.oognuyh.notificationservice.controller

import com.oognuyh.notificationservice.service.NotificationService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/notification")
class NotificationController(
    private val notificationService: NotificationService
) {

}