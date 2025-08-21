import { Routes } from '@angular/router';

export const routes: Routes = [  
    { path: '', loadComponent: () => import('./components/home/home.component').then(m => m.HomeComponent) },
    { path: 'signin', loadComponent: () => import('./components/signin/signin.component').then(m => m.SigninComponent) },
    { path: 'signup', loadComponent: () => import('./components/signup/signup.component').then(m => m.SignupComponent) },
    { path: 'dashboard', loadComponent: () => import('./components/dashboard/dashboard.component').then(m => m.DashboardComponent) },
    { path: 'profile', loadComponent: () => import('./components/edit-profile/edit-profile.component').then(m => m.EditProfileComponent) },
    { path: 'transactions', loadComponent: () => import('./components/transaction-history/transaction-history.component').then(m => m.TransactionHistoryComponent) },
    { path: 'send-money', loadComponent: () => import('./components/send-money/send-money.component').then(m => m.SendMoneyComponent) },
    { path: 'add-funds', loadComponent: () => import('./components/add-funds/add-funds.component').then(m => m.AddFundsComponent) },
    { path: 'pay-bill', loadComponent: () => import('./components/pay-bill/pay-bill.component').then(m => m.PayBillComponent) },
    { path: 'request-money', loadComponent: () => import('./components/request-money/request-money.component').then(m => m.RequestMoneyComponent) },
    { path: 'notifications', loadComponent: () => import('./components/notifications/notifications.component').then(m => m.NotificationsComponent) },
    { path: 'verification', loadComponent: () => import('./components/email-verifcation/email-verifcation.component').then(m => m.EmailVerificationComponent) },
    { path: 'createwallet', loadComponent: () => import('./components/create-wallet/create-wallet.component').then(m => m.CreateWalletComponent) },
    { path: 'invoices', loadComponent: () => import('./components/invoice/invoice.component').then(m => m.InvoiceComponent) },
    { path: 'budgets', loadComponent: () => import('./components/budget/budget.component').then(m => m.BudgetComponent) },
    // Lazy loaded routes for better performance
    { 
        path: 'analytics', 
        loadComponent: () => import('./components/analytics/analytics.component').then(m => m.AnalyticsComponent) 
    },
    { 
        path: 'recurring-payments', 
        loadComponent: () => import('./components/recurring-payments/recurring-payments.component').then(m => m.RecurringPaymentsComponent) 
    },
    { 
        path: 'security', 
        loadComponent: () => import('./components/security-settings/security-settings.component').then(m => m.SecuritySettingsComponent) 
    },
    { 
        path: 'contacts', 
        loadComponent: () => import('./components/contacts/contacts.component').then(m => m.ContactsComponent) 
    },
    { 
        path: 'settings', 
        loadComponent: () => import('./components/settings/settings.component').then(m => m.SettingsComponent) 
    },
    // Catch all route - redirect to home
    { path: '**', redirectTo: '' }
];
