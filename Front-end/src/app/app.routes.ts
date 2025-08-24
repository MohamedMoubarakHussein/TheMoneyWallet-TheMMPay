import { Routes } from '@angular/router';
import { AuthGuard } from './auth.guard';

export const routes: Routes = [  
    { path: '', loadComponent: () => import('./components/home/home.component').then(m => m.HomeComponent) },
    { path: 'signin', loadComponent: () => import('./components/signin/signin.component').then(m => m.SigninComponent) },
    { path: 'signup', loadComponent: () => import('./components/signup/signup.component').then(m => m.SignupComponent) },
    { path: 'dashboard', loadComponent: () => import('./components/dashboard/dashboard.component').then(m => m.DashboardComponent), canActivate: [AuthGuard] },
    { path: 'profile', loadComponent: () => import('./components/edit-profile/edit-profile.component').then(m => m.EditProfileComponent), canActivate: [AuthGuard] },
    { path: 'transactions', loadComponent: () => import('./components/transaction-history/transaction-history.component').then(m => m.TransactionHistoryComponent), canActivate: [AuthGuard] },
    { path: 'send-money', loadComponent: () => import('./components/send-money/send-money.component').then(m => m.SendMoneyComponent), canActivate: [AuthGuard] },
    { path: 'add-funds', loadComponent: () => import('./components/add-funds/add-funds.component').then(m => m.AddFundsComponent), canActivate: [AuthGuard] },
    { path: 'pay-bill', loadComponent: () => import('./components/pay-bill/pay-bill.component').then(m => m.PayBillComponent), canActivate: [AuthGuard] },
    { path: 'request-money', loadComponent: () => import('./components/request-money/request-money.component').then(m => m.RequestMoneyComponent), canActivate: [AuthGuard] },
    { path: 'notifications', loadComponent: () => import('./components/notifications/notifications.component').then(m => m.NotificationsComponent), canActivate: [AuthGuard] },
    { path: 'verification', loadComponent: () => import('./components/email-verifcation/email-verifcation.component').then(m => m.EmailVerificationComponent) },
    { path: 'createwallet', loadComponent: () => import('./components/create-wallet/create-wallet.component').then(m => m.CreateWalletComponent), canActivate: [AuthGuard] },
    { path: 'invoices', loadComponent: () => import('./components/invoice/invoice.component').then(m => m.InvoiceComponent), canActivate: [AuthGuard] },
    { path: 'budgets', loadComponent: () => import('./components/budget/budget.component').then(m => m.BudgetComponent), canActivate: [AuthGuard] },
    // Lazy loaded routes for better performance
    { 
        path: 'analytics', 
        loadComponent: () => import('./components/analytics/analytics.component').then(m => m.AnalyticsComponent), canActivate: [AuthGuard]
    },
    { 
        path: 'recurring-payments', 
        loadComponent: () => import('./components/recurring-payments/recurring-payments.component').then(m => m.RecurringPaymentsComponent), canActivate: [AuthGuard]
    },
    { 
        path: 'security', 
        loadComponent: () => import('./components/security-settings/security-settings.component').then(m => m.SecuritySettingsComponent), canActivate: [AuthGuard]
    },
    { 
        path: 'contacts', 
        loadComponent: () => import('./components/contacts/contacts.component').then(m => m.ContactsComponent), canActivate: [AuthGuard]
    },
    { 
        path: 'settings', 
        loadComponent: () => import('./components/settings/settings.component').then(m => m.SettingsComponent), canActivate: [AuthGuard]
    },
    {
        path: 'support',
        loadComponent: () => import('./components/support/support.component').then(m => m.SupportComponent), canActivate: [AuthGuard]
    },
    // Catch all route - redirect to home
    { path: '**', redirectTo: '' }
];
