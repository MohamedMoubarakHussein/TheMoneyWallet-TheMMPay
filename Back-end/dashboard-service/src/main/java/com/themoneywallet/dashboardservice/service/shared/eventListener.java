package com.themoneywallet.dashboardservice.service.shared;



import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.themoneywallet.dashboardservice.event.Event;
import com.themoneywallet.dashboardservice.event.EventType;
import com.themoneywallet.dashboardservice.service.implementation.DashboardDataService;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Data
@Slf4j
public class eventListener {

    private final DashboardDataService dashboardDataService;
  

    @KafkaListener(topics = {"auth-user-signup"}, groupId = "dashboard-service")
    public void handleEvents(Event event) {
        
        switch (event.getEventType()) {
            case EventType.AUTH_USER_SIGN_UP:
                    log.info(event.toString());
                    this.dashboardDataService.createUserDashboard(event);
                break;
             case EventType.AUTH_USER_LOGIN_SUCCESSED:
                  
                    this.dashboardDataService.userLogin(event);
                break;
             /*case EventType.CREATED_WALLET:
                    this.dashboardDataService.addUserWallet(event);
                break;*/
            /*   case EventType.WALLET_BALANCE_CHANGED:
                    this.dashboardDataService.updateWalletBalance(event);
                break;
              case EventType.WALLET_DELETED:
                    this.dashboardDataService.removeUserWallet(event);
                break;
              case EventType.TRANSFER_TRANSACTION_COMPLETED:
                    this.dashboardDataService.addRecentTransaction(event);
                break; */
            default:
                break;
        }
    }
}
