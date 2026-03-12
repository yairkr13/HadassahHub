import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { Button, Card, Input } from '@/components/ui';
import { useAuth } from '@/hooks/useAuth';
import { ROUTES } from '@/router/routes';
import { LoginRequest } from '@/types/auth.types';

export const LoginPage: React.FC = () => {
  const [formData, setFormData] = useState<LoginRequest>({
    email: '',
    password: '',
  });
  const [errors, setErrors] = useState<Partial<Record<keyof LoginRequest, string>>>({});
  const [isLoading, setIsLoading] = useState(false);
  const [touched, setTouched] = useState<Partial<Record<keyof LoginRequest, boolean>>>({});

  const { login, authError, clearAuthError } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  // Get the page user was trying to access before being redirected to login
  const from = location.state?.from?.pathname || ROUTES.COURSES;

  // Clear auth error when user starts typing (only on explicit user action)
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    
    // Clear auth error when user starts typing
    if (authError) {
      clearAuthError();
    }
    
    // Real-time validation for touched fields
    if (touched[name as keyof LoginRequest]) {
      const fieldError = validateField(name as keyof LoginRequest, value);
      setErrors(prev => ({ ...prev, [name]: fieldError }));
    }
  };

  const validateField = (name: keyof LoginRequest, value: string): string | undefined => {
    switch (name) {
      case 'email':
        if (!value) return 'Email is required';
        if (!/\S+@\S+\.\S+/.test(value)) return 'Please enter a valid email address';
        if (!value.includes('@edu.hac.ac.il') && !value.includes('@edu.jmc.ac.il')) {
          return 'Please use your academic email (@edu.hac.ac.il or @edu.jmc.ac.il)';
        }
        return undefined;
      case 'password':
        if (!value) return 'Password is required';
        if (value.length < 6) return 'Password must be at least 6 characters';
        return undefined;
      default:
        return undefined;
    }
  };

  const validateForm = (): boolean => {
    const newErrors: Partial<Record<keyof LoginRequest, string>> = {};
    
    Object.keys(formData).forEach((key) => {
      const fieldName = key as keyof LoginRequest;
      const error = validateField(fieldName, formData[fieldName]);
      if (error) {
        newErrors[fieldName] = error;
      }
    });

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Mark all fields as touched
    setTouched({ email: true, password: true });
    
    if (!validateForm()) return;

    setIsLoading(true);
    try {
      await login(formData);
      // Redirect to the page user was trying to access, or courses by default
      navigate(from, { replace: true });
    } catch (error: any) {
      // Error handling is now managed by AuthContext
      console.error('Login error:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    const fieldName = name as keyof LoginRequest;
    
    // Mark field as touched and validate
    setTouched(prev => ({ ...prev, [fieldName]: true }));
    const fieldError = validateField(fieldName, value);
    setErrors(prev => ({ ...prev, [fieldName]: fieldError }));
  };

  return (
    <div className="min-h-screen bg-background-soft flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-text-primary">Sign In</h1>
          <p className="mt-2 text-text-secondary">
            Welcome back to HadassahHub
          </p>
        </div>

        <Card>
          {authError && (
            <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
              <div className="flex items-center">
                <svg className="w-5 h-5 text-red-600 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
                </svg>
                <p className="text-red-800 text-sm">{authError}</p>
              </div>
            </div>
          )}
          
          <form onSubmit={handleSubmit} className="space-y-6">
            <Input
              label="Email"
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              onBlur={handleBlur}
              error={touched.email ? errors.email : undefined}
              placeholder="your.email@edu.hac.ac.il"
              required
            />

            <Input
              label="Password"
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              onBlur={handleBlur}
              error={touched.password ? errors.password : undefined}
              placeholder="Enter your password"
              required
            />

            <Button
              type="submit"
              variant="primary"
              className="w-full"
              loading={isLoading}
              disabled={Object.keys(errors).some(key => errors[key as keyof LoginRequest])}
            >
              Sign In
            </Button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-text-secondary">
              Don't have an account?{' '}
              <Link
                to={ROUTES.REGISTER}
                className="text-primary hover:text-primary/80 font-medium"
              >
                Register here
              </Link>
            </p>
          </div>
        </Card>
      </div>
    </div>
  );
};